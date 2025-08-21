package com.normalizar.teamplateEvent.useCase.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
// import com.normalizar.repositoryDynamoDB.ImplUseCaseDynamoDB;
// import com.normalizar.repositoryDynamoDB.IuseCaseDynamoDB;
import com.normalizar.serviceS3.UseCase.IuseCaseS3Service;
import com.normalizar.serviceS3.UseCase.impl.ImplUseCaseS3Service;
import com.normalizar.teamplateEvent.useCase.UseCaseTemplateEvent;
import com.normalizar.templateMemori.ImpleCaseMemory;
import com.normalizar.templateMemori.ItemplateCase;
import com.normalizar.thymeleaRender.ThymeleaRenderTeamplate;
import com.normalizar.utility.ImappingUseCase;
import com.normalizar.utility.impl.ImplMappingUseCase;


//import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

// Tomar un JSON normalizado (con datos del dispositivo).
// Mapearlo a un modelo de variables (Map<String, Object>) que Thymeleaf entienda.
// Escoger qué plantilla HTML usar (basada en el activo, que es el tipo de dispositivo).
// Renderizar ese HTML insertando los valores del modelo.
// Guardar o devolver ese HTML final (y en tu caso, subirlo a S3).

public class ImpleUseCaseTemplateEvent implements UseCaseTemplateEvent {
    private S3Client s3Client;
    //private DynamoDbClient client;
    private static final String TYPE_EVENT_TRIGGER_TEMPLATE = "TriggerJson";
    private ImappingUseCase imappingUseCase;
    private ItemplateCase itemplateCase;
    private IuseCaseS3Service iuseCaseS3Service;
    // private IuseCaseDynamoDB iuseCaseDynamoDB;
    // private DynamoDbClient DynamoCliente;
    public ImpleUseCaseTemplateEvent() {
        this.itemplateCase = new ImpleCaseMemory();
        this.s3Client = S3Client.create();
        this.imappingUseCase = new ImplMappingUseCase();
        this.iuseCaseS3Service = new ImplUseCaseS3Service();
        // this.iuseCaseDynamoDB = new ImplUseCaseDynamoDB();
        // this.DynamoCliente = DynamoDbClient.create();

    }

    @Override
    public void teamplate_created(S3EventNotificationRecord record, Context context) {

        String typeEvent = record.getS3().getConfigurationId();

        if (!TYPE_EVENT_TRIGGER_TEMPLATE.equals(typeEvent)) {
            System.out.println("El evento no coincide con el proceso designado, precesando otro mensaje");
            System.out.println("Evento disparado , nombre: "+ typeEvent);
        } else {
            String bucketName = record.getS3().getBucket().getName();
            String s3KeyJson = record.getS3().getObject().getKey();
            try {
                String s3Key = URLDecoder.decode(s3KeyJson, StandardCharsets.UTF_8.name());
                System.out.println("Momento de procesar El record " + " Bucket: " +  bucketName + "Path: "+ s3Key + "Type Event: " + typeEvent);
                // Obtenemos el Archivo HTML para mapearlo
                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build();
                try (InputStream html_file = s3Client.getObject(getObjectRequest)) {
                    //  Armar los datos del usurio para enviar archivo

                    String[] keySplit = s3Key.split("/");
                    System.out.println("Split : "+  keySplit);
                    if (keySplit.length < 4) {
                        throw new IllegalArgumentException("El path del objeto no tiene suficiente información");
                    }

                    String poolUserId = keySplit[3];
                    String tenantName = keySplit[2];
                    String activo = keySplit[keySplit.length - 2];
                    String fileName = keySplit[keySplit.length - 1];
                    String jobID = keySplit[1];
                    String formart = fileName.replaceFirst("\\.json$", "");
                    String fileNameHtml = formart + "_" + jobID + ".html";

                    byte[] html_file_memoria = html_file.readAllBytes();
                    String jsonString = new String(html_file_memoria, StandardCharsets.UTF_8);
                    Map<String, Object> modelo = imappingUseCase.mapJsonToThymeleafModel(jsonString);
                    String template = itemplateCase.selectTemplate(activo);

                    String html = ThymeleaRenderTeamplate.render(template, modelo);
                    byte[] byteHtml = html.getBytes(StandardCharsets.UTF_8);
                    InputStream archivoHtml = new ByteArrayInputStream(byteHtml);
                   
                    this.iuseCaseS3Service.uploaFile(poolUserId, tenantName,activo, archivoHtml, fileNameHtml, "text/html");
                    // try {
                    //     this.iuseCaseDynamoDB.UpdateItemDbBrainStausPath(jobID,tenantName,);
                    // } catch (Exception e) {
                    //     // TODO: handle exception
                    // }

                    System.out.println(modelo);
                    System.out.println("Se creo el archivo en S3");

                } catch (Exception e) {
                    context.getLogger().log("Error procesando HTML al obtenerlo: " + s3Key + " - " + e.getMessage());
                }

            } catch (Exception e) {
                context.getLogger()
                        .log("Error procesando : Fallo el proceso de creacion compleata de HTML FILE TEMPLATE : "
                                + s3KeyJson + " - " + e.getMessage());
            }

        }

    }

}
