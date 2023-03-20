package com.example.samplegateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SampleGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleGatewayApplication.class, args);
	}

}
