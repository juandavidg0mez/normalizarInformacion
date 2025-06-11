package com.normalizar.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizar.domain.ConclusionesObjetoFinal;
import com.normalizar.domain.ResponseObjetoFinal;
import com.normalizar.templateMemori.ImpleCaseMemory;
import com.normalizar.templateMemori.ItemplateCase;
import com.normalizar.thymeleaRender.ThymeleaRenderTeamplate;

import software.amazon.awssdk.services.s3.endpoints.internal.Value.Str;

public class LambdaGeneratePDF implements RequestStreamHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private ItemplateCase itemplateCase;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public LambdaGeneratePDF() {
        this.itemplateCase = new ImpleCaseMemory();
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

        try {
            APIGatewayV2HTTPEvent event = objectMapper.readValue(input, APIGatewayV2HTTPEvent.class);
            ConclusionesObjetoFinal requestData = objectMapper.readValue(event.getBody(),
                    ConclusionesObjetoFinal.class);

            // Construir el modelo de variables para el Thymeleaf
            Map<String, Object> modelo = new HashMap<>();
            // Datos del reporte Original

            // Esto lo podemos mandar a u meotodo que se encargue de como paremetro una key
            // un map y value (11/06/25)
            modelo.put("norma", requestData.getNorma());
            modelo.put("activo", requestData.getActivo());
            modelo.put("application", requestData.getApplication());
            modelo.put("tenant", requestData.getTenant());
            modelo.put("poolUserId", requestData.getPoolUserId());

            // Datos normalizados del Json que es la repuesta de la lambda
            modelo.put("dataNormalizada", requestData.getDataNormalizada());

            // Conclusiones del usuario
            modelo.put("conclusiones", requestData.getUserConclusions());

            // == ACA HACEMOS LAS GRAFICAAS (11/06/25)

            // Renderizr el HTML final con Thymeleaf

            String teamplateName = itemplateCase.selectTemplate(requestData.getNorma());
            String htmlfinal = ThymeleaRenderTeamplate.render(teamplateName, modelo);

            // Generar el PDF apartir HTML final
            byte[] pdfBytes = itemplateCase.generatePdfromHtml(htmlfinal);
            String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);

            String fileName = requestData.getFileName(); // Nombre sugerido por el front
            if (fileName == null || fileName.isEmpty()) {
                fileName = "reporte_" + UUID.randomUUID().toString() + ".pdf"; // Generar nombre único si no se
                                                                               // proporciona
            } else {
                if (!fileName.toLowerCase().endsWith(".pdf")) { // Asegurarse de que tenga la extensión .pdf
                    fileName += ".pdf";
                }
            }

            // Aca podriamos implementar la lambda de Subida de S3

            // https://e989ua8tf9.execute-api.us-east-1.amazonaws.com/dev/upLoadFile

            Map<String, String> payload = Map.of(
                    "base64File", pdfBase64,
                    "userPoolId",  requestData.getUserPoolId(),
                    "tenantName",  requestData.getTenantName(),
                    "fileName", fileName);
            String jsonInterno = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://e989ua8tf9.execute-api.us-east-1.amazonaws.com/dev/uploadDoc"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInterno))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Verifica el código de estado HTTP
            if (httpResponse.statusCode() != 200) {
                throw new RuntimeException("Error al invocar el servicio de normalización: " + httpResponse.body());
            }

            ResponseObjetoFinal responseObjetoFinal = new ResponseObjetoFinal(
                 "Se subió el Archivo PDF: " + fileName + " Correctamente. S3 Response: " + httpResponse.body());
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Access-Control-Allow-Origin", "*");

            APIGatewayV2HTTPResponse response = APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withBody(objectMapper.writeValueAsString(responseObjetoFinal))
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
