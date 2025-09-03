package com.normalizar.serviceS3.UseCase.impl;

import java.io.IOException;
import java.io.InputStream;

import com.normalizar.serviceS3.UseCase.IuseCaseS3Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class ImplUseCaseS3Service implements IuseCaseS3Service {

    private S3Client s3Client;

    public ImplUseCaseS3Service() {
        this.s3Client = S3Client.create();
    }

    @Override
    public String uploaFile(String userPoolId, String tennatName, String activo, InputStream file, String nameFile,  String contenType, String jobId)
            throws IOException {
        try {
            // ByteArrayInputStream fileForm = new
            // ByteArrayInputStream(file.readAllBytes());
            // String nombrePdf = nameFile.replaceAll(".xls", ".pdf");
            String keyS3 = "HTML_toGestion" + "/" + tennatName + "/" + userPoolId + "/reports/" + activo + "/" + jobId + "/" + nameFile;
            // Crear solicitud para subir el objeto
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket("my-spring-bucket-eligomez")
                    .key(keyS3)
                    .contentType(contenType)
                    .build();

            byte[] bytes = file.readAllBytes();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

            return "Se ha subido el archivo de forma correcta";
        } catch (Exception e) {
            throw new RuntimeException("Error al subir el archivo normalizado Json a S3", e);
        }
    }

}
