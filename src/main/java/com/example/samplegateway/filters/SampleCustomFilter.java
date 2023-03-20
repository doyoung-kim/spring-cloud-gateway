package com.example.samplegateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class SampleCustomFilter extends AbstractGatewayFilterFactory<SampleCustomFilter.Config> {

    public SampleCustomFilter() {
        super(Config.class);
    }
    @Override
    public GatewayFilter apply(Config config) {
        
        return ((exchange, chain) ->{
            ServerHttpRequest request=exchange.getRequest();
            
            ServerHttpResponse response=exchange.getResponse();
            
            log.info("Custom FRE Filter : 요청  ID -> {}",request.getId());

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Custom POST Filter : 응답 code -> {}",response.getStatusCode());
            }));

        }) ;
    }

    public static class Config {
    }
}
