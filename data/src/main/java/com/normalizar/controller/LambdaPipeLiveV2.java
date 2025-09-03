package com.normalizar.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.joda.time.Instant;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizar.domain.GraficaData;
import com.normalizar.domain.GraficaDataExi;
import com.normalizar.domain.Report;
import com.normalizar.domain.ResponseReport;
import com.normalizar.repositoryDynamoDB.ImplUseCaseDynamoDB;
import com.normalizar.repositoryDynamoDB.IuseCaseDynamoDB;
import com.normalizar.repositoryDynamoDB.entity.MetaDataReport;
import com.normalizar.serviceS3.UseCase.IuseCaseS3Service;
import com.normalizar.serviceS3.UseCase.impl.ImplUseCaseS3Service;
import com.normalizar.templateMemori.ImpleCaseMemory;
import com.normalizar.templateMemori.ItemplateCase;
import com.normalizar.thymeleaRender.ThymeleaRenderTeamplate;
import com.normalizar.utility.ImappingUseCase;
import com.normalizar.utility.impl.ImplMappingUseCase;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.http.HttpClient;

public class LambdaPipeLiveV2 implements RequestStreamHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private ItemplateCase itemplateCase;
    private IuseCaseDynamoDB iuseCaseDynamoDB;
    private final DynamoDbClient dbClient;
    private ImappingUseCase imappingUseCase;
    private IuseCaseS3Service iuseCaseS3Service;

    public LambdaPipeLiveV2() {
        this.itemplateCase = new ImpleCaseMemory();
        this.dbClient = DynamoDbClient.create();
        this.iuseCaseDynamoDB = new ImplUseCaseDynamoDB();
        this.imappingUseCase = new ImplMappingUseCase();
        this.iuseCaseS3Service = new ImplUseCaseS3Service();
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        try {
            APIGatewayV2HTTPEvent event = objectMapper.readValue(input, APIGatewayV2HTTPEvent.class);
            Report report = objectMapper.readValue(event.getBody(), Report.class);

            // MApeamos los objetos de la peticion (Request)

            String activo = report.getActivo();
            String tenant = report.getTenant();
            String poolUserId = report.getPoolUserId();
            String archivoToFront = report.getArchivoToFront();

            // Payload para lambda "Normalizar"
            Map<String, String> payloadInterno = Map.of("file", archivoToFront, "activo", activo);
            // <PajazoMental> y aca podemos scarlos a una base datos creo yo como un puerto
            // de salida (DB , *dinnamoDB*)
            String jsonInterno = objectMapper.writeValueAsString(payloadInterno);
            System.out.println("esto se esta enviando a la lambda de python");
            System.out.println(jsonInterno);
            // Armamos la el requesr y la cargamos con el payload
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://e989ua8tf9.execute-api.us-east-1.amazonaws.com/dev/inter_normalizar"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInterno))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Verifica el código de estado HTTP
            if (httpResponse.statusCode() != 200) {
                throw new RuntimeException("Error al invocar el servicio de normalización: " + httpResponse.body());
            }

            // --- Respuesta de la Lambda de normalización ---
            String jsonResult = httpResponse.body();

            // Para impresión/debug
            System.out.println("Respuesta del servicio de normalizacion:");
            System.out.println(jsonResult);

            try {
                objectMapper.readValue(jsonResult, Object.class);
                System.out.println("El JSON es válido.");
            } catch (Exception e) {
                throw new IllegalArgumentException("El JSON no es válido: " + e.getMessage());
            }

            String fileName = ""; // Nombre sugerido por el front
            if (fileName == null || fileName.isEmpty()) {
                fileName = "data_normalizada" + UUID.randomUUID().toString() + ".json"; // Generar nombre único si no se
                // proporciona
            } else {
                if (!fileName.toLowerCase().endsWith(".json")) { // Asegurarse de que tenga la extensión .pdf
                    fileName += ".json";
                }
            }
            System.out.println("Este json es de tipo: " + ((Object) jsonResult).getClass().getName());
            System.out.println(fileName);
            // Subir a S3 en base64 (aquí sí lo conviertes para S3)
            byte[] jsonBytes = jsonResult.getBytes(StandardCharsets.UTF_8);
            InputStream fileMemory = new ByteArrayInputStream(jsonBytes);

            this.iuseCaseS3Service.uploaFile(poolUserId, tenant, activo, fileMemory, fileName, "application/json", "Hola");
            System.out.println("Bloque de codigo subir archivo activado");

            // Usar el contenido para seguir procesando los datos
            Map<String, Object> allChartData = objectMapper.readValue(jsonResult, Map.class);

            GraficaData graficaCNData = objectMapper.convertValue(allChartData.get("GraficaCN"), GraficaData.class);
            GraficaData graficaRCNData = objectMapper.convertValue(allChartData.get("GraficaRCN"), GraficaData.class);
            GraficaDataExi graficaDataExi = objectMapper.convertValue(allChartData.get("GraficaExitacion"),
                    GraficaDataExi.class);

            // DynamoDB
            String report_id = UUID.randomUUID().toString();
            String timestamp = Instant.now().toString();

            // Create metaDataReport
            MetaDataReport metaDataReportDTO = new MetaDataReport();
            String pathJsonS3 = tenant + "/" + poolUserId + "/reports/" + activo + "/" + fileName;
            metaDataReportDTO.setActivo(activo);
            metaDataReportDTO.setTenantId(tenant);
            metaDataReportDTO.setPoolUserId(poolUserId);

            // el item no se edita por que el report_id es diferente
            metaDataReportDTO.setJobId(report_id);
            metaDataReportDTO.setS3JsonPath(pathJsonS3);
            metaDataReportDTO.setS3PdfPath("Missing But Soon");
            metaDataReportDTO.setTimestamp(timestamp);
            metaDataReportDTO.setEstado("NORMALIZADO");

            String saveItem = this.iuseCaseDynamoDB.CreateItem(metaDataReportDTO, dbClient, "tablaReports");
            System.out.println(saveItem);

            Map<String, Object> modelo = imappingUseCase.mapJsonToThymeleafModel(jsonResult);

            String template = itemplateCase.selectTemplate(activo);
            System.out.println(activo);

            String html = ThymeleaRenderTeamplate.render(template, modelo);
            byte[] byteHtml = html.getBytes(StandardCharsets.UTF_8);
            InputStream archivoHtml = new ByteArrayInputStream(byteHtml);
            String htmlFileName = "data_normalizada" + UUID.randomUUID().toString() + ".html"; 
            this.iuseCaseS3Service.uploaFile(poolUserId, tenant, activo, archivoHtml, htmlFileName, "text/html", "Hola");
            
            System.out.println(
                    "Estos son los objetos que esta tomando para graficar : " + template + "\ny el modelo" + modelo);
            ResponseReport reponseBody = new ResponseReport(metaDataReportDTO.getJobId(), html, graficaCNData,
                    graficaRCNData, graficaDataExi);

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Access-Control-Allow-Origin", "*");

            APIGatewayV2HTTPResponse response = APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withBody(objectMapper.writeValueAsString(reponseBody))
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
