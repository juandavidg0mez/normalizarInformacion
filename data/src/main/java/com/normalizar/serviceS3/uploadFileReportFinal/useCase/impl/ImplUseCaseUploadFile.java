package com.normalizar.serviceS3.uploadFileReportFinal.useCase.impl;

import java.io.IOException;
import java.io.InputStream;

import com.normalizar.serviceS3.uploadFileReportFinal.useCase.IUseCaseUploadFinalReport;

import software.amazon.awssdk.core.sync.RequestBody;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class ImplUseCaseUploadFile implements IUseCaseUploadFinalReport {
    private S3Client s3Client;

    public ImplUseCaseUploadFile() {
        this.s3Client = S3Client.create();
    }

    @Override
    public String uploadFileReportFinal(String s3key, InputStream archivoHtml) throws IOException {

        try {
            // el InputStream solo se puede leer una vez es un flujo de datos que se consume
            // al leerlo
            
            byte[] bytes = archivoHtml.readAllBytes();
            if (bytes.length == 0) {
                throw new RuntimeException("El archivo HTML está vacío");
            }
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket("my-spring-bucket-eligomez")
                    .key(s3key)
                    .contentType("text/html")
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
            return s3key;

        } catch (Exception e) {
            throw new RuntimeException("Error al subir el archivo HTML final a S3", e);
        }

    }

}
