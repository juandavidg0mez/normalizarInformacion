package com.normalizar.config.descomZip.Utils.controller;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizar.config.descomZip.Utils.domain.ZipFileToLambda;
import com.normalizar.config.descomZip.Utils.useCase.IUseCaseZip;
import com.normalizar.config.descomZip.Utils.useCase.impl.ImpleIUseCaseZip;

public class LambdaUploadFilesZips implements RequestHandler<SQSEvent, String > {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private IUseCaseZip iUseCaseZip;
    public LambdaUploadFilesZips(){
        this.iUseCaseZip = new ImpleIUseCaseZip();
    }
    @Override
    public String handleRequest(SQSEvent input, Context context) {
       for (SQSEvent.SQSMessage msg  : input.getRecords() ) {
        try {
            // es algo como python 
            Map<String, String> data =  objectMapper.readValue(msg.getBody(), Map.class);
            ZipFileToLambda zipFileToLambda = new ZipFileToLambda();
            zipFileToLambda.setActivo(data.get(""));
            iUseCaseZip.unZipAndUpload(null);
            return "Se realizo0";
        } catch (Exception e) {
            return "";
        }
       }
       return "Se ejecuto con exito ";
    }

}
