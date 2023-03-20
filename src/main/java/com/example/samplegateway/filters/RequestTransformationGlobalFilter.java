package com.example.samplegateway.filters;

import com.example.samplegateway.formation.RequestBodyTransformation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class RequestTransformationGlobalFilter implements GlobalFilter, Ordered {
    @Autowired
    private ModifyRequestBodyGatewayFilterFactory modifyRequestBodyFilter;
    @Autowired
    private RequestBodyTransformation requestBodyTrans;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return modifyRequestBodyFilter
                .apply(new ModifyRequestBodyGatewayFilterFactory.Config().setRewriteFunction(String.class, String.class, requestBodyTrans))
                .filter(exchange, chain);
    }

    @Override
    public int getOrder() {
        // return 20; // The order in which you want this filter to execute
        return RouteToRequestUrlFilter.ROUTE_TO_URL_FILTER_ORDER + 1;
    }

}
