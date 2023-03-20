# sample-gateway

본 프로젝트는 Spring-cloud-gateway를 기반으로 만들어져 있다.

스프링 클라우드 게이트웨이의 주 목적은 간단하면서도 확실한 방법으로 요청을 다른 API로 라우팅하고

보안, 모니터링, 메트릭, resiliency 같은 횡단 관심사를 모아 처리하고자 함이다.

## 용어

스프링 클라우드 게이트웨이는 스프링 부트 2.x, 스프링 웹플럭스, 프로젝트 리액터 기반이다.

그렇기 때문에 이미 익숙한 동기식 라이브러리(스프링 데이터,스프링 시큐리티 등)나 알만한 패턴들은 스프링 클라우드 >게이트웨이에 적용이 안될 수 있다.

때문에 이런 프로젝트에 익숙하지 않다면 다음과 같은 스프링 클라우드 게이트웨이에서 사용중인 용어에 대해서는 미리 >알아두는 것이 좋다.

- route predicate - 라우팅 규칙, 시간, host, method 등에 따라 라우팅 규칙을 서술한다.
- gateway filter - 라우팅 필터, 전달받은 HTTP요청이나 전송할 HTTP응답을 원하는대로 수정할 수 있다.
- global filter - 모든 route에 조건부로 적용되느 특수 필터, order에 따라 적용 순서가 결정된다.

## 프로젝트 구조

프로젝트의 패키지 구조는 다음과 같다.

```cmd
└── com.example.samplegateway
 ├── config         # 공통 설정
 ├── filters        # GatewayFilter, GlobalFilter
 ├── logging        # 요청,응답 로깅을 위한 Decorator
 ├── monitoring     # 모니터링 연계모듈
 ├── route          # 라우팅설정(route predicate)-java code로 설정
 ├── transform      # 요청, 응답 변환기
 │   └── model      # 요청, 응답 모델
 └── utils          # 공통유틸
```

## 테스트

request.http 파일의 내용을 보면 sample-api와 동시에 실행하여 http 요청을 보낼 수 있다. (vscode의 httpyac 등의 restapi 테스트 플러그인을 설치)
json혹은 xml형태의 요청을 보내어 테스트한다.

```
POST  http://localhost:8800/apisample/sample-api/v1/boards/list
Content-Type: application/json

{  
  "payLoad": "sample-api/v1/boards/list",
  "page": 1,
  "size": 10
}


POST http://localhost:8800/apisample/sample-api/v1/boards/list
Content-Type: text/xml

<?xml version="1.0" encoding="UTF-8"?>
<header xmlns="http://schemas.xmlsoap.org/soap/envelope"> 
   <commonHeader xmlns="">  
      <appName>SAMPLE_ID01</appName>
      <svcName >/sample-api/v1/boards/list</svcName>
      <fnName>boardListPaging</fnName> 
      <fnCd></fnCd>      
   </commonHeader>
</header>
<body xmlns="http://schemas.xmlsoap.org/soap/envelope"> 
   <service_request xmlns="">
      <bizHeader>
         <orderId>sampeId</orderId>
         <cbSvcName>api/boards</cbSvcName>
         <cbFnName>list</cbFnName>       
      </bizHeader>
      <boardPayload>
         <page>1</page>
         <size>10</size>
         <person>               
            <empNum>99102233</empNum>
            <empName>홍길동</empName>    
            <age>39</age>   
            <tel>010-6338-9898</tel>         
         </person>
         <person>               
            <empNum>99102243</empNum>
            <empName>배철수</empName>    
            <age>62</age>   
            <tel>010-5432-2367</tel>         
         </person>   
         <person>               
            <empNum>99102324</empNum>
            <empName>이수지</empName>    
            <age>29</age>   
            <tel>010-9377-2562</tel>         
         </person>                
        
      </boardPayload>     
   </service_request>
</body>

```



## 참고

- [스프링 클라우드 게이트웨이 공식문서 한글번역문](https://godekdls.github.io/Spring%20Cloud%20Gateway/contents/)
