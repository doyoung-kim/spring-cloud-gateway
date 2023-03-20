package com.example.samplegateway.config;

import com.example.samplegateway.filters.LoggingWebFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    // 활성화 하려면 주석을 푼다
    // Request/Response 로그 필터

    // @Bean
    // public LoggingWebFilter loggingWebFilter() {
    //     return new LoggingWebFilter();
    // }
    
}
