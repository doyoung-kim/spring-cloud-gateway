package com.example.samplegateway.monitoring;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MonRequestModel {
    private String svcCode;
    private String svcName;
    private String transType;
    private String uri;
    private String scheme;
    private String method;
    private String cookies;
    private String headers;
    private String remoteIp;
    private String parameters;
    private String payload;
 

    
}
