package com.normalizar.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

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

            List<UpdateItemClass> updates = objectMapper.readValue(
                    event.getBody(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, UpdateItemClass.class));

   
            
        // Recorrer y actualizar cada item
        for (UpdateItemClass update : updates) {
            iuseCaseDynamoDB.UpdateItemStatus(
                update.getSortKey(),
                update.getTenantName(),
                update.getStatusValue(),
                dynamoDbClient
            );
            context.getLogger().log("Actualizado: " + update.getSortKey());
        }


            Map<String, String> headers = new HashMap<>();

            headers.put("Content-Type", "application/json");
            headers.put("Access-Control-Allow-Origin", "*");

            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withBody(objectMapper.writeValueAsString(Map.of("message", "Items actualizados correctamente")))
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
