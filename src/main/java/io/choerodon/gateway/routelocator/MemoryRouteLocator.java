package io.choerodon.gateway.routelocator;

import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.util.StringUtils;

import java.util.*;

import io.choerodon.gateway.dto.RouteDTO;
import io.choerodon.gateway.helper.HelperZuulRoutesMemory;
import io.choerodon.gateway.mapper.RouteMapper;

/**
 * @author wanghao
 * @author zongw.lee@gmail.com
 * @since 2019/10/22
 */
public class MemoryRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator {

    private static final String PATH_SPE = "/";

    private ZuulProperties properties;

    private RouteMapper routeMapper;

    private HelperZuulRoutesMemory zuulRoutesMemory;

    public MemoryRouteLocator(String servletPath, ZuulProperties properties, RouteMapper routeMapper, HelperZuulRoutesMemory zuulRoutesMemory) {
        super(servletPath, properties);
        this.properties = properties;
        this.routeMapper = routeMapper;
        this.zuulRoutesMemory = zuulRoutesMemory;
    }

    @Override
    public void refresh() {
        doRefresh();
    }

    @Override
    protected Map<String, ZuulProperties.ZuulRoute> locateRoutes() {
        final Map<String, ZuulProperties.ZuulRoute> totalMap = new LinkedHashMap<>();
        Map<String, ZuulProperties.ZuulRoute> dbRouteMap = getRouteMapFromDB();
        Map<String, ZuulProperties.ZuulRoute> zuulRouteMap = properties.getRoutes();

        // 取出内存中存在，db中不存在的路由
        zuulRouteMap.forEach((k, v) -> {
            if (dbRouteMap.get(v.getId()) == null) {
                totalMap.put(v.getPath(), v);
            }
        });
        // 加上db中的路由
        dbRouteMap.forEach((k, v) -> totalMap.put(v.getPath(), v));
        Map<String, ZuulProperties.ZuulRoute> values = optimizeZuulAllRoutes(totalMap);
        zuulRoutesMemory.setRoutes(values);
        return values;
    }

    private Map<String, ZuulProperties.ZuulRoute> optimizeZuulAllRoutes(final Map<String, ZuulProperties.ZuulRoute> routesMap) {
        LinkedHashMap<String, ZuulProperties.ZuulRoute> values = new LinkedHashMap<>();
        routesMap.forEach((k, v) -> {
            String path = k;
            if (!path.startsWith("/")) {
                path = PATH_SPE + path;
            }
            if (StringUtils.hasText(this.properties.getPrefix())) {
                path = this.properties.getPrefix() + path;
                if (!path.startsWith("/")) {
                    path = PATH_SPE + path;
                }
            }
            values.put(path, v);
        });
        return values;
    }


    private Map<String, ZuulProperties.ZuulRoute> getRouteMapFromDB() {
        Map<String, ZuulProperties.ZuulRoute> routes = new LinkedHashMap<>();
        List<RouteDTO> dbRoutes = routeMapper.selectAll();
        dbRoutes.forEach(route -> {
            ZuulProperties.ZuulRoute zuulRoute = loadZuulRoute(route);
            routes.put(zuulRoute.getId(), zuulRoute);
        });
        return routes;
    }

    private ZuulProperties.ZuulRoute loadZuulRoute(RouteDTO route) {
        ZuulProperties.ZuulRoute zuulRoute = new ZuulProperties.ZuulRoute();
        zuulRoute.setId(route.getRouteId());
        zuulRoute.setServiceId(route.getServiceCode());
        zuulRoute.setPath(route.getBackendPath());
        zuulRoute.setStripPrefix(route.getStripPrefix());
        Set<String> sensitiveHeaders = new HashSet<>(Arrays.asList(route.getSensitiveHeaders().split(",")));
        zuulRoute.setSensitiveHeaders(sensitiveHeaders);
        return zuulRoute;
    }
}
