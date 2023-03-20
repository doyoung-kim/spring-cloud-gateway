package com.example.samplegateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonHeader {
    private String xmlns;    
    private String appName;    
    private String svcName;
    private String fnName;
    private String fnCd;
    
}
