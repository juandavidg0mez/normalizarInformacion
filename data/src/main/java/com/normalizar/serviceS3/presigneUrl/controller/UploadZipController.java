package com.normalizar.serviceS3.presigneUrl.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizar.serviceS3.presigneUrl.domain.GeneralRespone;
import com.normalizar.serviceS3.presigneUrl.domain.PresignedUrl;
import com.normalizar.serviceS3.presigneUrl.domain.dto.DtoUploadZip;
import com.normalizar.serviceS3.presigneUrl.useCase.IUseCasePresignedUrl;
import com.normalizar.serviceS3.presigneUrl.useCase.Impl.ImplUseCasePresignedUrl;

public class UploadZipController implements RequestStreamHandler{
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private IUseCasePresignedUrl iUseCasePresignedUrl;
    public UploadZipController(){
        this.iUseCasePresignedUrl = new ImplUseCasePresignedUrl();
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        try {
            APIGatewayV2HTTPEvent event = objectMapper.readValue(input, APIGatewayV2HTTPEvent.class);
            DtoUploadZip  dtoUploadZip = objectMapper.readValue(event.getBody(), DtoUploadZip.class);

            String fileNameCodec  = dtoUploadZip.getActivo() + dtoUploadZip.getFileName();
            
            byte[] decodedBytes = Base64.getDecoder().decode(dtoUploadZip.getFile());
            InputStream fileStream = new ByteArrayInputStream(decodedBytes);

            PresignedUrl presignedUrl = new PresignedUrl();
            presignedUrl.setTenantName(dtoUploadZip.getTenantName());
            presignedUrl.setUserPoolId(dtoUploadZip.getUserPoolId());
            presignedUrl.setActivo(dtoUploadZip.getActivo());
            presignedUrl.setFileName(fileNameCodec);
            presignedUrl.setFileZip(fileStream);
            presignedUrl.setFileSize(decodedBytes.length);
            String result =this.iUseCasePresignedUrl.upLoadFileForUrl(presignedUrl);
            System.out.println("Resultado subida ZIP: " + result);

            GeneralRespone generalRespone = new GeneralRespone("Se a subido el archivo Zip");

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Access-Control-Allow-Origin", "*");

            APIGatewayV2HTTPResponse response = APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withBody(objectMapper.writeValueAsString(generalRespone))
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
