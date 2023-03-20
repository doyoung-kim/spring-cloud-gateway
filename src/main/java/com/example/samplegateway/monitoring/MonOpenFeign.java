package com.example.samplegateway.monitoring;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="monitoring", url= "${api.mon.url}")
public interface MonOpenFeign {

 
    @PostMapping("/monitoring")
	public void monitoring( String data);
	


}
