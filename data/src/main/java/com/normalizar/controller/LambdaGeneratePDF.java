package com.normalizar.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizar.domain.ConclusionesObjetoFinal;
import com.normalizar.templateMemori.ImpleCaseMemory;
import com.normalizar.templateMemori.ItemplateCase;

public class LambdaGeneratePDF implements RequestStreamHandler{
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private  ItemplateCase itemplateCase;
    public LambdaGeneratePDF(){
        this.itemplateCase = new ImpleCaseMemory();
    }
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

        try{
            APIGatewayV2HTTPEvent event = objectMapper.readValue(input, APIGatewayV2HTTPEvent.class);
            ConclusionesObjetoFinal conclusionesObjetoFinal = objectMapper.readValue(event.getBody(), ConclusionesObjetoFinal.class);
            
            Map<Integer, String> conclucionesMap = conclusionesObjetoFinal.getConclucion();
            String teamplateCrudo = conclusionesObjetoFinal.getTemplate();

            ByteArrayOutputStream pdfByte = itemplateCase.generatePdfromHtml(teamplateCrudo);

            // Aca podriamos implementar la lambda de Subida de S3

            // https://e989ua8tf9.execute-api.us-east-1.amazonaws.com/dev/upLoadFile

            


            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'handleRequest'");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
