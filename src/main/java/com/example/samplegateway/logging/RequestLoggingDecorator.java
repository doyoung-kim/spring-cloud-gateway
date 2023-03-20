package com.example.samplegateway.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;

import com.example.samplegateway.monitoring.MonOpenFeign;
import com.example.samplegateway.monitoring.MonRequestModel;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.scheduling.annotation.Async;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class RequestLoggingDecorator extends ServerHttpRequestDecorator {

    private MonOpenFeign mon;
    
    public RequestLoggingDecorator(ServerHttpRequest delegate, MonOpenFeign mon) {
        super(delegate);
        this.mon = mon;
       
    }

    @Override
    public Flux<DataBuffer> getBody() {
        
        
        HttpMethod method = getMethod();
        if (method == HttpMethod.GET ||method == HttpMethod.DELETE) {
            
            logging(null);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        return super.getBody()
            .doOnNext(dataBuffer -> {
                try {
                    Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                    String body = IOUtils.toString(baos.toByteArray(), "UTF-8");
                    
                    logging(body);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        });
    }

    private void logging(String body) {
        MonRequestModel monRequestModel =  MonRequestModel.builder()
            .svcCode("002")
            .svcName("demo-gateway")
            .transType("request")
            .uri(getPath().pathWithinApplication().value())
            .scheme(getURI().getScheme())
            .method(getMethod().toString())
            .cookies(getCookies().toString())
            .headers(getHeaders().toString())
            .remoteIp(getRemoteAddress().toString())
            .parameters(getQueryParams().toString())
            .payload(body)
            .build();

        // log.info("----- monRequestModel : {}", monRequestModel.toString());
        
        sendAsync(monRequestModel.toString());
        log.info("\n\n start-------333-------------RequestLoggingDecorator----------------------------- \n " +
                    "- Request path: {}\n "+
                    "- scheme:{}\n " +
                    "- Request method: {}\n "+
                    "- Cookies:{}\n "+
                    "- Request headers: {}\n "+
                    "- Remote IP address:{}\n "+
                    "- Form parameters:{}\n "+
                    "- Request body: {}\n "+
            "end---------333----------RequestLoggingDecorator------------------------------ \n ",
            getPath().pathWithinApplication().value(), getURI().getScheme(), getMethod(), getCookies(), getHeaders(), getRemoteAddress(), getQueryParams(), body);

    }

    @Async
    public void sendAsync(String data){
        log.info("===== sendAsync data:{}", data);
        try{
            mon.monitoring(data);
        }catch(Exception ex){
            log.error("error Mon sendData :", ex.getMessage());
        }

    }



    
    

}
