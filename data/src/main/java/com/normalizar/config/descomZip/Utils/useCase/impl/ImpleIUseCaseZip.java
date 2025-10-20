package com.normalizar.config.descomZip.Utils.useCase.impl;

import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import com.normalizar.config.descomZip.Utils.domain.ZipFileToLambda;
import com.normalizar.config.descomZip.Utils.useCase.IUseCaseZip;
import com.normalizar.repositoryDynamoDB.ImplUseCaseDynamoDB;
import com.normalizar.repositoryDynamoDB.IuseCaseDynamoDB;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
// gestiona todos los ducmentos y actualiza esl estado del documento  que descomprime entonce no actualiza nada mas 
public class ImpleIUseCaseZip implements IUseCaseZip {
    private static final String BUCKET = "my-spring-bucket-eligomez";
    private static final String UPLOADS_EXCEL_A_S3 = "uploadsExcels";
    private DynamoDbClient client;
    private IuseCaseDynamoDB iuseCaseDynamoDB;
    private S3Client s3Client;

    public ImpleIUseCaseZip() {
        this.s3Client = S3Client.create();
        this.client =  DynamoDbClient.create();
        this.iuseCaseDynamoDB = new ImplUseCaseDynamoDB();
    }

    @Override
    public void unZipAndUpload(ZipFileToLambda zipFileToLambda) {
        InputStream zipSQS = zipFileToLambda.getZipStream();
        try (ZipInputStream zis = new ZipInputStream(zipSQS)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    // readAllBytes lee todos los archivos internos del archivo comprimido
                    byte[] bytes = zis.readAllBytes();
                    
                    String outPutKey = UPLOADS_EXCEL_A_S3 + "/" +
                            zipFileToLambda.getJobId() + "/" +
                            zipFileToLambda.getTenantName() + "/" +
                            zipFileToLambda.getUserPoolId() + "/" +
                            zipFileToLambda.getActivo() + "/" +
                            entry.getName();
                    System.out.println("Ruta donde se descomprime el arhcivo :" + outPutKey);
                    s3Client.putObject(PutObjectRequest.builder()
                            .bucket(BUCKET)
                            .key(outPutKey)
                            .metadata(Map.of("jobId", zipFileToLambda.getJobId()))
                            .build(), RequestBody.fromBytes(bytes));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Archivo Zip descomprimido y listo para normalizar mensajes de SQS");
        }
    }

    private HeadObjectResponse getHeadObject(S3Client s3Client, String bucket, String key) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3Client.headObject(headObjectRequest);
    }

    @Override
    public void procesarRecord(S3EventNotificationRecord record, Context context) {
        String bucketName = record.getS3().getBucket().getName();
        String s3Key = record.getS3().getObject().getKey();
        System.out.println("Momento de procesar El record " + bucketName + s3Key);
        try {
            HeadObjectResponse headObjectResponse = getHeadObject(s3Client, bucketName, s3Key);
            Map<String, String> objecZipMetaData = headObjectResponse.metadata();
            String jobId = objecZipMetaData.get("jobid");
            System.out.println("Este es el codigo de JOD ID: "+jobId);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            try (InputStream zInputStream = s3Client.getObject(getObjectRequest)) {
                String[] keySplit = s3Key.split("/");
                System.out.println("Split : "+  keySplit);
                if (keySplit.length < 4) {
                    throw new IllegalArgumentException("El path del objeto no tiene suficiente información");
                }

                ZipFileToLambda zipFileToLambda = new ZipFileToLambda();
                zipFileToLambda.setJobId(jobId);
                zipFileToLambda.setZipStream(zInputStream);
                zipFileToLambda.setTenantName(keySplit[1]);
                zipFileToLambda.setUserPoolId(keySplit[2]);
                zipFileToLambda.setActivo(keySplit[3]);

                unZipAndUpload(zipFileToLambda);
                this.iuseCaseDynamoDB.UpdateItemDbBrainStatus(jobId, keySplit[1], "DESCOMPRIMIDO", client);
                System.out.println("Se actualizo la base de datos del lote :" + jobId + "Tenant Name: " + keySplit[1]);


            }
        } catch (Exception e) {
           context.getLogger().log("Error procesando el archivo ZIP: " + s3Key + " - " + e.getMessage());
        }
    }
    // ZipInputStream zis = new ZipInputStream(inputStream);
    // ZipEntry entry;

    // while ((entry = zis.getNextEntry()) != null) {
    // System.out.println("Nombre: " + entry.getName());
    // System.out.println("¿Es directorio? " + entry.isDirectory());
    // }
    // Aunque S3 no tiene carpetas reales como un sistema de archivos, sí
    // interpreta / en las claves (keys) como si fueran "carpetas". Por eso,
    // si usas entry.getName() tal como viene, S3 mantendrá la estructura visual del
    // ZIP.
}
