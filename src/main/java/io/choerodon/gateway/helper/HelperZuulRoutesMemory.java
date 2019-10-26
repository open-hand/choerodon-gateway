package io.choerodon.gateway.helper;

import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author wanghao
 * @author zongw.lee@gmail.com
 * @since 2019/10/22
 */
@Component
public class HelperZuulRoutesMemory {

    private Map<String, ZuulRoute> routes = new LinkedHashMap<>();

    public Map<String, ZuulRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<String, ZuulRoute> routes) {
        this.routes = routes;
    }
}