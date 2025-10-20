package com.normalizar.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizar.domain.QueryReportList;
import com.normalizar.repositoryDynamoDB.ImplUseCaseDynamoDB;
import com.normalizar.repositoryDynamoDB.IuseCaseDynamoDB;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class LambdaQueryReportsList implements RequestStreamHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private DynamoDbClient dynamoDbClient;
    private IuseCaseDynamoDB iuseCaseDynamoDB;

    public LambdaQueryReportsList() {
        this.dynamoDbClient = DynamoDbClient.create();
        this.iuseCaseDynamoDB = new ImplUseCaseDynamoDB(); // Inicializa según sea necesario
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        try {
            APIGatewayV2HTTPEvent event = objectMapper.readValue(input, APIGatewayV2HTTPEvent.class);
            QueryReportList queryReportList = objectMapper.readValue(event.getBody(), QueryReportList.class);
            String tenantName = queryReportList.getTenantName();
            String userPoolId = queryReportList.getUserPoolId();
            String status = queryReportList.getStatus();

            List<Map<String, String>> items = this.iuseCaseDynamoDB.consultarReportesPendientes(tenantName, userPoolId,
                    status, dynamoDbClient);
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Access-Control-Allow-Origin", "*");
            context.getLogger().log("Tenant=" + tenantName + ", User Pool ID=" + userPoolId + ", estado=" + status);
            context.getLogger().log("Items encontrados=" + items.size());

            APIGatewayV2HTTPResponse response = APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withBody(objectMapper.writeValueAsString(items))
                    .withHeaders(headers)
                    .build();
            output.write(objectMapper.writeValueAsBytes(response));
        } catch (Exception e) {
            Map<String, String> errorBody = Map.of("error", e.getMessage());

            Map<String, String> errorHeaders = new HashMap<>();
            errorHeaders.put("Content-Type", "application/json");
            errorHeaders.put("Access-Control-Allow-Origin", "*");

            APIGatewayV2HTTPResponse errorResponse = APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(500)
                    .withBody(objectMapper.writeValueAsString(errorBody))
                    .withHeaders(errorHeaders)
                    .build();

            output.write(objectMapper.writeValueAsBytes(errorResponse));
        }
    }
}
