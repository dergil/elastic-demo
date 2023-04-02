package com.github.dergil.elasticdemo.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {
//  Logs request method and URI
//  Placed at the end of a potential filter chain, so that the result of all alterations can be observed
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Request: {} {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI());
        return chain.filter(exchange);
    }
}
