package com.normalizar.controller;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

//import org.thymeleaf.context.Context;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizar.templateMemori.ImpleCaseMemory;
import com.normalizar.templateMemori.ItemplateCase;
import com.normalizar.thymeleaRender.ThymeleaRenderTeamplate;

// Esta manejador de peticion contiene la invocacion del la normalizacion en pocas palabras es la primera que tiene contacto con el exterior
// Recibe un JSON con metadatos + archivo en base64.

// Invoca otra Lambda (en Python) pasándole el archivo para normalizarlo.

// Recibe la respuesta de la Lambda Python (JSON).

// Mezcla la respuesta con los otros datos recibidos.

// Renderiza una vista HTML usando Thymeleaf (plantilla) con todos esos datos combinados.

// Retorna el HTML como String (por ejemplo, para usarlo en una vista previa o correo).

public class LambdaPipeLine implements RequestHandler<Map<String, Object>, String> {

    // Esto crea un cliente que puede invocar otras lambda adentro de la misma
    private final AWSLambda lambdaClient = AWSLambdaClientBuilder.defaultClient();
    private ItemplateCase itemplateCase;
    
    public LambdaPipeLine(){
        this.itemplateCase = new ImpleCaseMemory();
    }

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        try {

            // Aca toma los datos del request
            String norma = (String) input.get("norma");
            String activo = (String) input.get("activo");
            String tipoAplication = (String) input.get("tipoAplication");
            String tennat = (String) input.get("tennat");
            String poolUserId = (String) input.get("poolUserId");
            String archivoBase64 = (String) input.get("archivoBase64");

            // Payload para lambda "Normalizar"
            Map<String, String> payloadPython = Map.of("file_base64", archivoBase64);
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
            @SuppressWarnings("unchecked")
            Map<String, Object> dataNormalizada = new ObjectMapper().readValue(jsonResult, Map.class);
            Map<String, Object> modelo = new HashMap<>();

            modelo.put("norma", norma);
            modelo.put("activo", activo);
            modelo.put("tipoAplication", tipoAplication);
            modelo.put("tennat", tennat);
            modelo.put("poolUserId", poolUserId);
            modelo.put("archivoBase64", archivoBase64);

            modelo.putAll(dataNormalizada);

            String template = itemplateCase.selectTemplate(norma);

            String html = ThymeleaRenderTeamplate.render(template, modelo);
            return html;

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Error procesando la solicitud\"}";
        }
    }
             
    

}
