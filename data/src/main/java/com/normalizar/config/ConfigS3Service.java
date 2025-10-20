package com.normalizar.config;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class ConfigS3Service {
     public static S3Client createS3Client() {
        //String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
        //String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        //AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
                .region(Region.US_EAST_1)
                //.credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
