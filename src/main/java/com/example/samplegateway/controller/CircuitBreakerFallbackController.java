package com.example.samplegateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CircuitBreakerFallbackController {
    
    @RequestMapping("/circuitbreakerfallback")
	public String circuitbreakerfallback() {
		return "======This is a fallback";
	}
    
}
