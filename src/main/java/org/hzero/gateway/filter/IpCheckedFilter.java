package org.hzero.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/5 7:38 下午
 */
@Order(Integer.MIN_VALUE)
public class IpCheckedFilter implements WebFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(IpCheckedFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        LOGGER.info("[IPcheck] skip ip check");
        return chain.filter(exchange);
    }

}
