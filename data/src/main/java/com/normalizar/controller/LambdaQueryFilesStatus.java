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
import com.normalizar.domain.ItemQuery;
import com.normalizar.repositoryDynamoDB.ImplUseCaseDynamoDB;
import com.normalizar.repositoryDynamoDB.IuseCaseDynamoDB;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class LambdaQueryFilesStatus implements RequestStreamHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private DynamoDbClient dynamoDbClient;
    private IuseCaseDynamoDB iuseCaseDynamoDB;

    public LambdaQueryFilesStatus() {
        this.dynamoDbClient = DynamoDbClient.create();
        this.iuseCaseDynamoDB = new ImplUseCaseDynamoDB();
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        try {
            APIGatewayV2HTTPEvent event = objectMapper.readValue(input, APIGatewayV2HTTPEvent.class);
            ItemQuery itemQuery = objectMapper.readValue(event.getBody(), ItemQuery.class);

            String tenant_name = itemQuery.getTenant_name();
            String job_id = itemQuery.getJob_id();
            String estado = itemQuery.getEstado();

            List<Map<String, String>> items = this.iuseCaseDynamoDB.consultarLotesPendientes(tenant_name, job_id,
                    estado,
                    dynamoDbClient);

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Access-Control-Allow-Origin", "*");
            context.getLogger().log("Tenant=" + tenant_name + ", job=" + job_id + ", estado=" + estado);
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
