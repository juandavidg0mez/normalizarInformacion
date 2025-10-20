package com.normalizar.serviceS3.abstracObject.useCase.impl;

import java.util.Base64;

import com.normalizar.serviceS3.abstracObject.useCase.IUseCaseObjectAbstracS3;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
public class ImplObjectAbstracS3 implements IUseCaseObjectAbstracS3 {
    private final S3Client s3Client;
    private final String BUCKET_NAME = "my-spring-bucket-eligomez";

    public ImplObjectAbstracS3() {
        this.s3Client = S3Client.create();
    }

    @Override
    public String consultObjectS3(String s3Key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(s3Key)
                    .build(); 

            try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest)) {
                byte[] bytes = s3Object.readAllBytes();
                return Base64.getEncoder().encodeToString(bytes);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el archivo de S3 con key " + s3Key, e);
        }
    }
}
