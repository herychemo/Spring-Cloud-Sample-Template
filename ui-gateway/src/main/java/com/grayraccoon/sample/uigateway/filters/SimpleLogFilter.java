package com.grayraccoon.sample.uigateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

public class SimpleLogFilter implements GlobalFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleLogFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Set<URI> uris = exchange.getAttributeOrDefault(GATEWAY_ORIGINAL_REQUEST_URL_ATTR, Collections.emptySet());
        String originalUri = (uris.isEmpty()) ? "Unknown" : uris.iterator().next().toString();
        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        URI routeUri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String method = exchange.getRequest().getMethod().name();

        LOGGER.info("{} - Incoming request {} is routed to id: {}, uri: {}",
                method, originalUri, route.getId(), routeUri);

        //return chain.filter(exchange).then(Mono.fromRunnable(() -> {}));
        return chain.filter(exchange);
    }

}
