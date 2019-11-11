package io.choerodon.gateway.filter.route;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static io.choerodon.core.variable.RequestVariableHolder.*;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import io.choerodon.gateway.config.GatewayProperties;

/**
 * 添加token和routeRuleCode到请求header
 *
 * @author flyleft
 */
public class HeaderWrapperFilter extends ZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderWrapperFilter.class);

    private static final String ATTR_TOKEN = "token";

    private GatewayProperties gatewayHelperProperties;

    public HeaderWrapperFilter(GatewayProperties gatewayHelperProperties) {
        this.gatewayHelperProperties = gatewayHelperProperties;
    }

    private static final int HEADER_WRAPPER_FILTER = -1;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return HEADER_WRAPPER_FILTER;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String token = (String) request.getAttribute(HEADER_JWT);
        boolean isPublic = Boolean.parseBoolean((String) request.getAttribute("isPublic"));
        if (isPublic) {
            //移除Authorization header,防止其他服务解析jwt时报不合法的token
            ctx.addZuulRequestHeader(HEADER_TOKEN, "");
        }
        if (StringUtils.isEmpty(token)) {
            LOGGER.info("Request get empty jwt , request uri: {} method: {}", request.getRequestURI(), request.getMethod());
        } else {
            ctx.addZuulRequestHeader(HEADER_TOKEN, token);
            // 网关转发负载均衡时使用
            request.setAttribute(ATTR_TOKEN, token);
            if (gatewayHelperProperties.isEnabledJwtLog()) {
                LOGGER.info("Request get jwt , request uri: {} method: {} JWT: {}", request.getRequestURI(), request.getMethod(), token);
            }
        }
        String routeRuleCode = (String) request.getAttribute(HEADER_ROUTE_RULE);
        if (!StringUtils.isEmpty(routeRuleCode)) {
            LOGGER.info("Request set route-rule-code to header , request uri: {} method: {} code: {}", request.getRequestURI(), request.getMethod(), routeRuleCode);
            ctx.addZuulRequestHeader(HEADER_ROUTE_RULE, routeRuleCode);
        }
        return null;
    }

}
