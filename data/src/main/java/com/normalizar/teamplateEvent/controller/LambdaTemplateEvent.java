package com.normalizar.teamplateEvent.controller;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import com.normalizar.teamplateEvent.useCase.UseCaseTemplateEvent;
import com.normalizar.teamplateEvent.useCase.impl.ImpleUseCaseTemplateEvent;

public class LambdaTemplateEvent implements RequestHandler<S3Event, String> {
    private UseCaseTemplateEvent useCaseTemplateEvent;
    public LambdaTemplateEvent(){
        this.useCaseTemplateEvent = new ImpleUseCaseTemplateEvent();
    }
    @Override
    public String handleRequest(S3Event s3Event, Context context) {
       try {
            for (S3EventNotificationRecord record : s3Event.getRecords()) {
                this.useCaseTemplateEvent.teamplate_created(record, context);
            }
            return "Todos los archivos HTML han sido generados correctamente";
       } catch (Exception e) {
            context.getLogger().log("Error general al procesar los eventos S3: " + e.getMessage());
            return "Fallo la lambda al procesar los archivos HTML";
       }
    }
    
}
