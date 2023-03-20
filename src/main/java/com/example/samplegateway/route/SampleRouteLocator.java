package com.example.samplegateway.route;

import com.example.samplegateway.filters.CustomGlobalFilter;
import com.example.samplegateway.filters.RequestTransformationGlobalFilter;
import com.example.samplegateway.filters.SampleCustomFilter;
import com.example.samplegateway.formation.RequestBodyTransformation;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SampleRouteLocator {
 
	private final SampleCustomFilter sampleCustomFilter;
	private final RequestBodyTransformation bodyFormation;



    @Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		 
	
		return builder.routes()
				.route("sample-api",r -> r.path("/apisample/**")								
					.filters(f -> 																	
						f.filter(sampleCustomFilter.apply(new SampleCustomFilter.Config()))
					)
					.uri("http://global-svc.default.svc.cluster.local:8088")					
				)
				.build();
	}

	// @Bean
	// public GlobalFilter customGlobalFilter() {
	// 	return new CustomGlobalFilter();
	// }

	@Bean
	public GlobalFilter requestTransformationGlobalFilter() {
		return new RequestTransformationGlobalFilter();
	}


 

	

    

    
}
