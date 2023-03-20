package com.example.samplegateway.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;

import com.example.samplegateway.monitoring.MonOpenFeign;
import com.example.samplegateway.monitoring.MonResponseModel;

import org.apache.commons.io.IOUtils;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.scheduling.annotation.Async;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class ResponseLoggingDecorator extends ServerHttpResponseDecorator {

    private long startTime;
    
    private MonOpenFeign mon;
 
    public ResponseLoggingDecorator(ServerHttpResponse delegate, long startTime, MonOpenFeign mon) {
       super(delegate);
       this.startTime= startTime;
       this.mon= mon;
        
    }
 
   @Override
   public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
      Flux<DataBuffer> buffer = Flux.from(body);
      return super.writeWith(buffer.doOnNext(dataBuffer -> {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try {
            Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
            String bodyRes = IOUtils.toString(baos.toByteArray(), "UTF-8");
            log.info("=======Response==writeWith=====================");

            logging(bodyRes);


         } catch (IOException e) {
            e.printStackTrace();
         } finally {
            try {
               baos.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }));
   }
   private void logging(String body) {
      MonResponseModel momResponseModel =  MonResponseModel.builder()
         .svcCode("002")
         .svcName("demo-gateway")
         .transType("response")
         .statusCode(getStatusCode().value())
         .headers(getHeaders().toString())
          .payload(body)
          .build();

      // log.info("----- momResponseModel : {}", momResponseModel.toString());
      
      sendAsync(momResponseModel.toString());
      log.info("\n\n start-------333-------------ResponseLoggingDecorator----------------------------- \n " +
                  "- Response({} ms)" +
                  "- status:{}\n " +
                  "- Request header: {}\n "+
                  "- Response body: {}\n "+
          "end---------333----------ResponseLoggingDecorator------------------------------ \n ",
          (System.currentTimeMillis() - startTime), getStatusCode().value(),  getHeaders(),  body);

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