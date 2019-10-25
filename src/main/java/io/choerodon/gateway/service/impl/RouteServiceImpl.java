package io.choerodon.gateway.service.impl;

import io.choerodon.gateway.dto.RouteDTO;
import io.choerodon.gateway.mapper.RouteMapper;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RefreshScope
public class RouteServiceImpl {
    private RouteMapper routeMapper;
    private ZuulProperties zuulProperties;

    public RouteServiceImpl(RouteMapper routeMapper, ZuulProperties zuulProperties) {
        this.routeMapper = routeMapper;
        this.zuulProperties = zuulProperties;
    }

    @PostConstruct
    public void processRouteData(){
        for (ZuulProperties.ZuulRoute route : zuulProperties.getRoutes().values()){
            RouteDTO example = new RouteDTO();
            example.setServiceCode(route.getServiceId());
            RouteDTO result = routeMapper.selectOne(example);
            if (result == null){
                example.setBackendPath(route.getPath());
                routeMapper.insertSelective(example);
            } else {
                result.setBackendPath(route.getPath());
                routeMapper.updateByPrimaryKeySelective(result);
            }
        }
    }
}
