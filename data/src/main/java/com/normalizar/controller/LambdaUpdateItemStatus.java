package com.normalizar.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizar.domain.UpdateItemClass;
import com.normalizar.repositoryDynamoDB.ImplUseCaseDynamoDB;
import com.normalizar.repositoryDynamoDB.IuseCaseDynamoDB;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class LambdaUpdateItemStatus implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
    private static final Logger logger = LoggerFactory.getLogger(LambdaUpdateItemStatus.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private IuseCaseDynamoDB iuseCaseDynamoDB;
    private DynamoDbClient dynamoDbClient;

    public LambdaUpdateItemStatus() {
        this.dynamoDbClient = DynamoDbClient.create();
        this.iuseCaseDynamoDB = new ImplUseCaseDynamoDB();
    }

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        try {

            UpdateItemClass updateItemClass = objectMapper.readValue(event.getBody(), UpdateItemClass.class);
            
            String jobId = updateItemClass.getSortKey();
            String tenantName = updateItemClass.getTenantName();
            String status = updateItemClass.getStatusValue();
            
            this.iuseCaseDynamoDB.UpdateItemStatus(jobId, tenantName, status, dynamoDbClient);
            
            context.getLogger().log(
                    "Se actualizo el item con jobId: " + jobId + ", tenantName: " + tenantName + ", status: " + status);
            logger.info("Se actualizo el item con jobId: {}, tenantName: {}, status: {}", jobId, tenantName, status);
            
            Map<String, String> headers = new HashMap<>();
            
            headers.put("Content-Type", "application/json");
            headers.put("Access-Control-Allow-Origin", "*");

            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withBody(objectMapper.writeValueAsString("{\"message\": \"Item actualizado correctamente\"}"))
                    .withHeaders(headers)
                    .build();

        } catch (Exception e) {
            Map<String, String> errorBody = Map.of("error", e.getMessage());

            String errorBodyString;
            try {
                errorBodyString = objectMapper.writeValueAsString(errorBody);
            } catch (Exception parseException) {
                errorBodyString = "{\"error\":\"Error interno grave al procesar el error.\"}";
            }

            Map<String, String> errorHeaders = new HashMap<>();
            errorHeaders.put("Content-Type", "application/json");
            errorHeaders.put("Access-Control-Allow-Origin", "*");

            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(500)
                    .withBody(errorBodyString)
                    .withHeaders(errorHeaders)
                    .build();
        }
    }

}
