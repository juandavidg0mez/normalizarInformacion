package com.normalizar.serviceS3.presigneUrl.useCase.Impl;



import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import com.normalizar.repositoryDynamoDB.ImplUseCaseDynamoDB;
import com.normalizar.repositoryDynamoDB.IuseCaseDynamoDB;
import com.normalizar.repositoryDynamoDB.entity.MetaDataReport;
import com.normalizar.serviceS3.presigneUrl.domain.PresignedUrl;
import com.normalizar.serviceS3.presigneUrl.useCase.IUseCasePresignedUrl;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;



// Subir por medio de una URL prefirmada y actualizar el estado en DB
// Tener en cuenta el jobId
// Crear otra tabla ?¿
public class ImplUseCasePresignedUrl implements IUseCasePresignedUrl {
    private S3Client s3Client;
    private DynamoDbClient dynamoDbClient;
    private IuseCaseDynamoDB iuseCaseDynamoDB;
    private static final String REPORT_TABLE_NAME = "report_SQS_brain";
    public ImplUseCasePresignedUrl(){
        this.s3Client = S3Client.create();
        this.dynamoDbClient = DynamoDbClient.create();
        this.iuseCaseDynamoDB = new ImplUseCaseDynamoDB();
    }

    // podemos ver que el objeto a subir no esta en putObjectRequest sino en el putObject 
    @Override
    public String upLoadFileForUrl(PresignedUrl presignedUrl) {
        try {
            
            String keyS3 = String.format("ZipFilesToSQS/%s/%s/%s/%s", 
                    presignedUrl.getTenantName(),
                    presignedUrl.getUserPoolId(),
                    presignedUrl.getActivo(),
                    presignedUrl.getFileName());
            String jobId = UUID.randomUUID().toString();
            String timestamp = Instant.now().toString();

            Map<String, String> jobIdMetadata= Map.of("jobId" , jobId);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket("my-spring-bucket-eligomez")
                    .key(keyS3)
                    .contentType("application/zip")
                    .metadata(jobIdMetadata)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(presignedUrl.getFileZip(), presignedUrl.getFileSize()));
            MetaDataReport metaDataReport = new MetaDataReport();
            metaDataReport.setTenantId(presignedUrl.getTenantName());
            metaDataReport.setActivo(presignedUrl.getActivo());
            metaDataReport.setPoolUserId(presignedUrl.getUserPoolId());
            metaDataReport.setJobId(jobId);
            metaDataReport.setTimestamp(timestamp);
            metaDataReport.setS3ZipPath(keyS3);
            metaDataReport.setEstado("INICIO COLA");

            this.iuseCaseDynamoDB.CreateItem(metaDataReport, dynamoDbClient, REPORT_TABLE_NAME);
            System.out.println("Se Actulizo base de datos en inicio de Cola");

            
            return keyS3;

            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al subir archivo ZIP: " + e.getMessage();

        }
    }

}
