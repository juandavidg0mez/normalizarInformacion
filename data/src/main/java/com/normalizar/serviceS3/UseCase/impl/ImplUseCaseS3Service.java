package com.normalizar.serviceS3.UseCase.impl;

import java.io.IOException;
import java.io.InputStream;

import com.normalizar.serviceS3.UseCase.IuseCaseS3Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class ImplUseCaseS3Service implements IuseCaseS3Service {

    private S3Client s3Client;
    public ImplUseCaseS3Service(){
        this.s3Client = S3Client.create();
    }

    @Override
    public String uploaFile(String userPoolId, String tennatName,String activo, InputStream file, String nameFile, String contenType)
            throws IOException {
        try {
            // ByteArrayInputStream fileForm = new ByteArrayInputStream(file.readAllBytes());
            // String nombrePdf = nameFile.replaceAll(".xls", ".pdf");
            String keyS3 = tennatName + "/" + userPoolId + "/reports/"+ activo + "/" + nameFile;
            // Crear solicitud para subir el objeto
            byte[] fileBytes = file.readAllBytes();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket("my-spring-bucket-eligomez")
                    .key(keyS3)
                    .contentType(contenType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));

            return "Se ha subido el archivo de forma correcta";
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF desde Excel", e);
        }
    }
    
}
