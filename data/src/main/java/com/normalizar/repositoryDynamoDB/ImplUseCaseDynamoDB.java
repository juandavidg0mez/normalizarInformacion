package com.normalizar.repositoryDynamoDB;

import java.util.HashMap;
import java.util.Map;


import com.normalizar.repositoryDynamoDB.entity.MetaDataReport;

import software.amazon.awssdk.services.dynamodb.DynamoDBClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDBException;
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
    public String CreateItem(MetaDataReport metaDataReport, DynamoDBClient client, String tableName) {
        // Lo que tengo que hacer es mapear el lo que venga de afuera Externamente, para
        // poder enviar esto Al repository
        // Debemos tener encuenta los nombres del objeto que viene del exterior
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("tenant_id", AttributeValue.builder().s(metaDataReport.getTenantId()).build());
            item.put("reporte:is", AttributeValue.builder().s(metaDataReport.getReporteId()).build());
            item.put("norma", AttributeValue.builder().s(metaDataReport.getNorma()).build());
            item.put("activo", AttributeValue.builder().s(metaDataReport.getActivo()).build());
            item.put("application", AttributeValue.builder().s(metaDataReport.getApplication()).build());
            item.put("poolUserId", AttributeValue.builder().s(metaDataReport.getPoolUserId()).build());
            item.put("s3_json_path", AttributeValue.builder().s(metaDataReport.getS3JsonPath()).build());
            item.put("s3_pdf_path", AttributeValue.builder().s(metaDataReport.getS3PdfPath()).build());
            item.put("fecha_creacion", AttributeValue.builder().s(metaDataReport.getTimestamp()).build());
            item.put("estado", AttributeValue.builder().s(metaDataReport.getEstado()).build());

            PutItemRequest putItemRequest = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(item)
                    .build();
                    
            client.putItem(putItemRequest);
            System.out.println("Metadatos del reporte guardados exitosamente para tenant: "
                    + metaDataReport.getTenantId() + ", reporteId: " + metaDataReport.getReporteId());
            return "Metadatos del reporte guardados en DynamoDB para tenant: " + metaDataReport.getTenantId()
                    + ", reporteId: " + metaDataReport.getReporteId();
        } catch (DynamoDBException e) {
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

}
