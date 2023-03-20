package com.example.samplegateway.formation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map.Entry;

import com.example.samplegateway.model.CommonHeader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;
import org.json.XML;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RequestBodyTransformation implements RewriteFunction<String, String> {

    @Autowired
    private ObjectMapper mapper;

    @Value("${router.global.service.url}")
    private String routerGlobalServiceUrl;

    @Override
    public Publisher<String> apply(ServerWebExchange exchange, String requestBody) {
        log.info("===SimpleRewriteFunction==requestBody=== {}", requestBody);
        if (requestBody == null) {
            return Mono.empty();
        }
        boolean isJson = isApplicationJson(exchange);
        String jsonBody = null;
        if (isJson) {
            jsonBody = requestBody;
        } else {
            try {
                jsonBody = xml2Json(requestBody);
            } catch (JsonProcessingException e) {

                e.printStackTrace();
            }
        }

        log.info("===jsonBody=== {}", jsonBody);
        // Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // CommonHeader commonHeader = getCommonHeader(gson, jsonBody );
        CommonHeader commonHeader = getCommonheaderByJackson(mapper, jsonBody);

        log.info("===commonHeader=== {}", commonHeader.toString());
        String modifiedJsonBody = null;
        try {
            modifiedJsonBody = modifyJsonBody(jsonBody);
        } catch (JsonProcessingException e) {

            e.printStackTrace();
        }

        changeRequestUrl(exchange, commonHeader.getSvcName());
        return Mono.just(modifiedJsonBody);

    }

    private String modifyJsonBody(String jsonBody) throws JsonMappingException, JsonProcessingException {
        JsonNode node = mapper.readTree(jsonBody);
        // JsonNode boardPayloadNode =
        // node.path("body").path("service_request").path("boardPayload");
        JsonNode boardPayloadNode = node.at("/body/service_request/boardPayload");
        int size = boardPayloadNode.get("size").asInt();
        int page = boardPayloadNode.get("page").asInt();
        log.info("==[origin]====size {} ==page : {}", size, page);
        ObjectNode objNodeBoard = (ObjectNode) boardPayloadNode;
        objNodeBoard.put("page", 5)
                .put("size", 7)
                .putObject("address")
                .put("city", "Seattle")
                .put("state", "Washington")
                .put("country", "United States");

        ArrayNode arr = mapper.createArrayNode();
        // ArrayNode에 primitive type element 추가
        arr.add("a");
        arr.add("b");
        arr.add("c");
        // ObjectNode에 ArrayNode 추가
        objNodeBoard.set("alphabet", arr);

        // Iterator<String> fieldNames = node.fieldNames();
        // while (fieldNames.hasNext()) {
        // String fieldName = fieldNames.next();
        // JsonNode fieldValue = node.get(fieldName);
        // System.out.println(fieldName + " = " + fieldValue.asText());
        // }

        log.info("==============findValue=================");
        JsonNode findValueNode = boardPayloadNode.findValue("person");
        log.info("====findValueNode : {}", findValueNode.toPrettyString());
        printElement(findValueNode);

        // ObjectNode addedNode = ((ObjectNode) boardPayloadNode).putObject("address");

        // log.info("====boardObjNode : {}", boardObjNode.toPrettyString());
        log.info("====boardPayloadNode1 : {}", boardPayloadNode.toPrettyString());
        objNodeBoard.remove("address");
        objNodeBoard.remove("person");
        objNodeBoard.remove("alphabet");
        objNodeBoard.put("page", 2)
        .put("size", 7);


        // log.info("====boardPayloadNode2 : {}", boardPayloadNode.toPrettyString());

        log.info("====node : {}", node.toPrettyString());

        return node.toPrettyString();
    }

    private void printElement(JsonNode node) {
        if (node.isArray()) {
           
            for (JsonNode arrayElement : node) {
                Iterator<Entry<String, JsonNode>> iterator = arrayElement.fields();

                while(iterator.hasNext()){
                    log.info("key: {}", iterator.next().getKey());
                    log.info("value: {}", iterator.next().getValue().asText());
                }
            }
            
        } else {

            log.info("value: {}", node.asText());
        }

    }

    private CommonHeader getCommonHeader(Gson gson, String jsonStr) {
        JsonElement jsonElement = JsonParser.parseString(jsonStr)
                .getAsJsonObject().get("header")
                .getAsJsonObject().get("commonHeader");

        return gson.fromJson(jsonElement, CommonHeader.class);

    }

    private CommonHeader getCommonheaderByJackson(ObjectMapper mapper, String jsonStr) {

        CommonHeader commonHeader = null;
        try {
            JsonNode node = mapper.readTree(jsonStr);
            String commonHeaderJson = node.at("/header/commonHeader").toString();
            commonHeader = mapper.readValue(commonHeaderJson, CommonHeader.class);
        } catch (JsonProcessingException e) {

            e.printStackTrace();
        }

        return commonHeader;

    }

    private void changeRequestUrl(ServerWebExchange exchange, String path) {
        StringBuilder strBuilder = new StringBuilder(routerGlobalServiceUrl).append(path);
        String newUrl = strBuilder.toString();
        log.info("==== newUrl : {}", newUrl);

        try {
            exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, new URI(newUrl));
        } catch (URISyntaxException e) {

            e.printStackTrace();
        }
    }

    private String xml2Json(String xmlBody) throws JsonProcessingException {
        JSONObject jObject = XML.toJSONObject(xmlBody);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        Object jsonObj = mapper.readValue(jObject.toString(), Object.class);
        String jsonStr = mapper.writeValueAsString(jsonObj);

        return jsonStr;

    }

    private boolean isApplicationJson(ServerWebExchange exchange) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        HttpMethod method = serverHttpRequest.getMethod();
        String contentType = serverHttpRequest.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        if (method == HttpMethod.POST && MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType)) {
            return true;
        }
        return false;

    }

}
