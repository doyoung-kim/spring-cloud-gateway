package com.example.samplegateway.filters;

import java.util.List;

import com.example.samplegateway.logging.RequestLoggingDecorator;
import com.example.samplegateway.logging.ResponseLoggingDecorator;
import com.example.samplegateway.monitoring.MonOpenFeign;
import com.example.samplegateway.utils.BeanUtils;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j

public class LoggingWebFilter implements WebFilter {

   private MonOpenFeign mon;

   private String ignorePatterns;
   private boolean logHeaders;
   private boolean useContentLength;

   public LoggingWebFilter() {
      this.mon = (MonOpenFeign) BeanUtils.getBean(MonOpenFeign.class);
   }

   @Override
   public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

      final long startTime = System.currentTimeMillis();
      List<String> header = exchange.getRequest().getHeaders().get("Content-Length");
      // if (useContentLength && (header == null || header.get(0).equals("0"))) {

      // log.info("=====================Request: method={}, uri={}, headers={}",
      // exchange.getRequest().getMethod(),
      // exchange.getRequest().getURI().getPath(),
      // exchange.getRequest().getHeaders());

      // }

      // log.info("=====================Request: method={}, uri={}, headers={}",
      // exchange.getRequest().getMethod(),
      // exchange.getRequest().getURI().getPath(),
      // exchange.getRequest().getHeaders());
      ServerWebExchangeDecorator exchangeDecorator = new ServerWebExchangeDecorator(exchange) {
         @Override
         public ServerHttpRequest getRequest() {
            return new RequestLoggingDecorator(super.getRequest(), mon);
         }

         @Override
         public ServerHttpResponse getResponse() {
            return new ResponseLoggingDecorator(super.getResponse(), startTime, mon);
         }
      };
      return chain.filter(exchangeDecorator)
            .doOnSuccess(aVoid -> {
               log.info("=======LoggingWebFilter========doOnSuccess======");

            })
            .doOnError(throwable -> {
               log.info("=======LoggingWebFilter========doOnError======");

            });

   }
}