package com.example.samplegateway.monitoring;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MonResponseModel {
    private String svcCode;
    private String svcName;
    private String transType;   
    private int  statusCode;
    private String headers;
    private String payload;
 

    
}
