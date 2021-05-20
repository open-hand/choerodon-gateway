package io.choerodon.gateway.filter;

import java.util.List;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseHeaders;
import org.hzero.core.util.ServerRequestUtils;
import org.hzero.core.util.UrlUtils;
import org.hzero.gateway.helper.config.GatewayHelperProperties;
import org.hzero.gateway.helper.entity.RequestContext;
import org.hzero.gateway.helper.filter.CommonRequestCheckFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import io.choerodon.core.oauth.CustomUserDetails;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/5/20
 * @Modified By:
 */
@Service
@Primary
public class CommonRequestCheckC7nFilter extends CommonRequestCheckFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonRequestCheckC7nFilter.class);

    private List<String> parameterTenantId;
    @Autowired
    private GatewayHelperProperties helperProperties;

    @PostConstruct
    public void init() {
        this.parameterTenantId = helperProperties.getFilter().getCommonRequest().getParameterTenantId();
        super.init();
    }

    @Override
    public boolean run(RequestContext context) {
        LOGGER.info("===================================c7n CommonRequestCheckC7nFilter=================");
        CustomUserDetails details = context.getCustomUserDetails();
        Long tenantId = getTenantId(context);
        if (tenantId != null) {
            details.setTenantId(tenantId);
            context.setCustomUserDetails(details);
        }
        return super.run(context);
    }


    private Long getTenantId(final RequestContext context) {
        Long tenantId = UrlUtils.parseLongValueFromUri(context.getTrueUri(), context.getPermission().getPath(), parameterTenantId);
        if (tenantId != null) {
            return tenantId;
        }
        String value = ServerRequestUtils.getHeaderValue(context.getServletRequest(), BaseHeaders.H_TENANT_ID);
        if (StringUtils.isNotBlank(value)) {
            return Long.parseLong(value);
        }
        return null;
    }
}
