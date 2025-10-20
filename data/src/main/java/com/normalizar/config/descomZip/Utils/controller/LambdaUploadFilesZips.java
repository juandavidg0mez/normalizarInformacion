package com.normalizar.config.descomZip.Utils.controller;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;

import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;

import com.normalizar.config.descomZip.Utils.useCase.IUseCaseZip;
import com.normalizar.config.descomZip.Utils.useCase.impl.ImpleIUseCaseZip;


// Descomprimimos y guardamos los archivos en optra ruta de s3(dicha ruta contiene un evento que se envia directamente a SQS)
public class LambdaUploadFilesZips implements RequestHandler<S3Event, String> {

    private IUseCaseZip iUseCaseZip;

    public LambdaUploadFilesZips() {
        this.iUseCaseZip = new ImpleIUseCaseZip();

    }

    @Override
    public String handleRequest(S3Event s3Event, Context context) {
        try {
            for (S3EventNotificationRecord record : s3Event.getRecords()) {
                this.iUseCaseZip.procesarRecord(record, context);
            }
            return "Todos los archivos ZIP procesados correctamente";
        } catch (Exception e) {
            context.getLogger().log("Error general al procesar los eventos S3: " + e.getMessage());
            return "Fallo la lambda al procesar los archivos ZIP";
        }
    }
}
