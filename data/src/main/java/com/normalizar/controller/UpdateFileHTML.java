package com.normalizar.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizar.domain.PdfRenderRequest;
import com.normalizar.serviceS3.UseCase.IuseCaseS3Service;
import com.normalizar.serviceS3.UseCase.impl.ImplUseCaseS3Service;

// Estafuncion recibe un archivo html en base64, lo decodifica, y lo re escribe en S3 dado que ya tiene las graficas insertadas

public class UpdateFileHTML implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
        private static final ObjectMapper objectMapper = new ObjectMapper();
        private IuseCaseS3Service iuseCaseS3Service;

        public UpdateFileHTML() {
                this.iuseCaseS3Service = new ImplUseCaseS3Service();
        }

        @Override
        public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {
                try {
                        // Parsear el body a tu objeto DTO
                        PdfRenderRequest request = objectMapper.readValue(input.getBody(), PdfRenderRequest.class);

                        // Convertir archivoHtml a InputStream (ya que tu servicio lo pide)
                        byte[] decodedBytes = Base64.getDecoder().decode(request.getArchivoHtml());
                        InputStream inputStream = new ByteArrayInputStream(decodedBytes);
                        // Llamar a tu servicio que sube el archivo a S3
                        String result = iuseCaseS3Service.uploaFile(
                                        request.getPoolUserId(),
                                        request.getTenant_id(),
                                        request.getActivo(),
                                        inputStream,
                                        request.getFileName(),
                                        "text/html; charset=UTF-8",
                                        request.getReport_id());

                        // Devolver respuesta 200 con el resultado
                        Map<String, String> headers = new HashMap<>();

                        headers.put("Content-Type", "application/json");
                        headers.put("Access-Control-Allow-Origin", "*");

                        return APIGatewayV2HTTPResponse.builder()
                                        .withStatusCode(200)
                                        .withBody(objectMapper.writeValueAsString(
                                                        Map.of("message",
                                                                        "html actualizados correctamente + " + result)))
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
