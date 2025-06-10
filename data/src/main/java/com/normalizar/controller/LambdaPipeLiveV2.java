package com.normalizar.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;



import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizar.domain.Report;
import com.normalizar.domain.ResponseReport;
import com.normalizar.templateMemori.ImpleCaseMemory;
import com.normalizar.templateMemori.ItemplateCase;
import com.normalizar.thymeleaRender.ThymeleaRenderTeamplate;

public class LambdaPipeLiveV2 implements RequestStreamHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final AWSLambda lambdaClient = AWSLambdaClientBuilder.defaultClient();
    private ItemplateCase itemplateCase;

    public LambdaPipeLiveV2() {
        this.itemplateCase = new ImpleCaseMemory();
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        try {
            APIGatewayV2HTTPEvent event = objectMapper.readValue(input, APIGatewayV2HTTPEvent.class);
            Report report = objectMapper.readValue(event.getBody(), Report.class);

            // MApeamos los objetos de la peticion (Request)
            String norma = report.getNorma();
            String activo = report.getActivo();
            String application = report.getApplication();
            String tennat = report.getTenant();
            String poolUserId = report.getPoolUserId();
            String archivoToFront = report.getArchivoToFront();

            // Payload para lambda "Normalizar"
            Map<String, String> payloadPython = Map.of("file_base64", archivoToFront);
            // <PajazoMental> y aca podemos scarlos a una base datos creo yo como un puerto
            // de salida (DB , *dinnamoDB*)

            // Armamos la lambda y la cargamos con el payload
            InvokeRequest request = new InvokeRequest()
                    .withFunctionName("arn:aws:lambda:us-east-1:240435918890:function:normalizarExcel")
                    .withInvocationType("RequestResponse")
                    .withPayload(new ObjectMapper().writeValueAsString(payloadPython));

            // ejecutamos la lambda que estamos invocando la armamos y la activamos como un
            // arma del minecraft
            InvokeResult result = lambdaClient.invoke(request);

            String jsonResult = new String(result.getPayload().array(), StandardCharsets.UTF_8);
            // @SuppressWarnings("unchecked")
            Map<String, Object> dataNormalizada = objectMapper.readValue(jsonResult, Map.class);
            Map<String, Object> modelo = new HashMap<>();

            modelo.put("norma", norma);
            modelo.put("activo", activo);
            modelo.put("tipoAplication", application);
            modelo.put("tennat", tennat);
            modelo.put("poolUserId", poolUserId);
            modelo.put("archivoBase64", archivoToFront);

            modelo.putAll(dataNormalizada);

            String template = itemplateCase.selectTemplate(norma);

            String html = ThymeleaRenderTeamplate.render(template, modelo);

            ResponseReport reponseBody = new ResponseReport(html);

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
