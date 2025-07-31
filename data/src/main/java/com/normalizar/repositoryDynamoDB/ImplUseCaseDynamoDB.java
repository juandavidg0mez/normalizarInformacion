package com.normalizar.repositoryDynamoDB;

import java.util.HashMap;

import java.util.Map;

import com.normalizar.repositoryDynamoDB.entity.MetaDataReport;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class ImplUseCaseDynamoDB implements IuseCaseDynamoDB {

    @Override
    public String CreateItem(String tenant, String poolUserId) {
        try {

            return "Se genero el item";
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unimplemented method 'CreateItem'");
        }
    }                           

    @Override
    public String CreateItem(MetaDataReport metaDataReport, DynamoDbClient client, String tableName) {
        // Lo que tengo que hacer es mapear el lo que venga de afuera Externamente, para
        // poder enviar esto Al repository
        // Debemos tener encuenta los nombres del objeto que viene del exterior

        try {

            Map<String, AttributeValue> item = new HashMap<>();
            addAttributeIfNotNull(item, "tenant_id", metaDataReport.getTenantId());
            addAttributeIfNotNull(item, "job_id", metaDataReport.getJobId());
            addAttributeIfNotNull(item, "activo", metaDataReport.getActivo());
            addAttributeIfNotNull(item, "pool_user_id", metaDataReport.getPoolUserId());
            addAttributeIfNotNull(item, "s3_json_path", metaDataReport.getS3JsonPath());
            addAttributeIfNotNull(item, "s3_pdf_path", metaDataReport.getS3PdfPath());
            addAttributeIfNotNull(item, "s3_zip_path", metaDataReport.getS3ZipPath());
            addAttributeIfNotNull(item, "s3_final_pdf_path", metaDataReport.getS3FinalPdfReport());
            addAttributeIfNotNull(item, "s3_final_zip_path", metaDataReport.getS3ZipFileFinalReport());
            addAttributeIfNotNull(item, "fecha_creacion", metaDataReport.getTimestamp());
            addAttributeIfNotNull(item, "estado", metaDataReport.getEstado());

            if (!item.containsKey("tenant_id")|| !item.containsKey("job_id")) {
                throw new IllegalArgumentException("la clave de particion 'tenant_id' y 'job_id' no pueden ser nuls");
            }

            PutItemRequest putItemRequest = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(item)
                    .build();

            client.putItem(putItemRequest);
            System.out.println("Metadatos del reporte guardados exitosamente para tenant: "
                    + metaDataReport.getTenantId() + ", reporteId: " + metaDataReport.getJobId());

            return "Metadatos del reporte guardados en DynamoDB para tenant: " + metaDataReport.getTenantId()
                    + ", reporteId: " + metaDataReport.getJobId();

        } catch (DynamoDbException e) {
            // Log the specific DynamoDB exception for better debugging
            System.err.println("Error de DynamoDB al guardar ítem: " + e.getMessage());
            // Re-throw a custom runtime exception or a more generic one
            throw new RuntimeException("Failed to save report metadata to DynamoDB due to a database error.", e);
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            System.err.println("Error inesperado al guardar ítem en DynamoDB: " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred while saving report metadata.", e);
        }
    }

    private void addAttributeIfNotNull(Map<String, AttributeValue> item, String key , String value){
        if (value != null && !value.isEmpty()) {
            item.put(key, AttributeValue.builder().s(value).build());
        }
    }

}
