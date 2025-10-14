package com.normalizar.serviceS3.uploadFileReportFinal.controller;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizar.serviceS3.uploadFileReportFinal.domain.RequestUploadFile;
import com.normalizar.serviceS3.uploadFileReportFinal.useCase.IUseCaseUploadFinalReport;
import com.normalizar.serviceS3.uploadFileReportFinal.useCase.impl.ImplUseCaseUploadFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

//  Un controlado Lambda que se activa cauando API Gateway recibe un apeticion HTTP
//  Recibe un objeto JSON con la llave S3 y el archivo HTML en Base64
public class LambdaUploadFileReportFinal implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private IUseCaseUploadFinalReport iUseCaseUploadFinalReport;

    public LambdaUploadFileReportFinal() {
        this.iUseCaseUploadFinalReport = new ImplUseCaseUploadFile();
    }

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {
        try {
            RequestUploadFile request = objectMapper.readValue(input.getBody(), RequestUploadFile.class);
            byte[] decodedBytes = Base64.getDecoder().decode(request.getArchivoHtml());
            InputStream inputStream = new ByteArrayInputStream(decodedBytes);
            String s3Key = request.getS3key();
            String result = iUseCaseUploadFinalReport.uploadFileReportFinal(s3Key, inputStream);
            Map<String, String> headers = new HashMap<>();

            headers.put("Content-Type", "text/html; charset=UTF-8");
            headers.put("Access-Control-Allow-Origin", "*");

            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withBody(objectMapper.writeValueAsString(
                            Map.of("message",
                                    "html actualizados correctamente " + result)))
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
