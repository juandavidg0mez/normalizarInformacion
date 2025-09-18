package com.normalizar.serviceS3.abstracObject.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.normalizar.serviceS3.abstracObject.domian.RequestObject;
import com.normalizar.serviceS3.abstracObject.domian.ResponseObject;
import com.normalizar.serviceS3.abstracObject.useCase.IUseCaseObjectAbstracS3;
import com.normalizar.serviceS3.abstracObject.useCase.impl.ImplObjectAbstracS3;


/**
 * Clase LambdaObjectAbstracS3
 * ---------------------------
 * Esta clase implementa un manejador de AWS Lambda para consultar objetos en S3.
 * La Lambda se invoca desde API Gateway, recibe un JSON con un "s3Key",
 * consulta el objeto en S3 a través de un caso de uso y devuelve la respuesta en JSON.
 */

public class LambdaObjectAbstracS3 implements RequestStreamHandler {
    private ObjectMapper objectMapper = new ObjectMapper();
    private IUseCaseObjectAbstracS3 iUseCaseObjectAbstracS3;

    public LambdaObjectAbstracS3() {
        this.iUseCaseObjectAbstracS3 = new ImplObjectAbstracS3();
    }

        /**
     * Método principal que AWS Lambda ejecuta cuando recibe una invocación.
     *
     * @param input   Flujo de entrada con el evento JSON recibido desde API Gateway
     * @param output  Flujo de salida donde se escribe la respuesta a devolver
     * @param context Contexto de la ejecución de Lambda (información de logs, requestId, etc.)
     */

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        try {
            APIGatewayV2HTTPEvent event = objectMapper.readValue(input, APIGatewayV2HTTPEvent.class);
            RequestObject requestObject = objectMapper.readValue(event.getBody(), RequestObject.class);
            String s3Key = requestObject.getS3Key();
            String result = null;
            System.out.println("Consultando objeto S3 con key: " + s3Key);
            if (s3Key != null && !s3Key.isEmpty()) {
                result = iUseCaseObjectAbstracS3.consultObjectS3(s3Key);
            }
            ResponseObject responseObject = new ResponseObject(result);
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Access-Control-Allow-Origin", "*");

            APIGatewayV2HTTPResponse response = APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withBody(objectMapper.writeValueAsString(responseObject))
                    .withHeaders(headers)
                    .build();

            // Escribir salida
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
