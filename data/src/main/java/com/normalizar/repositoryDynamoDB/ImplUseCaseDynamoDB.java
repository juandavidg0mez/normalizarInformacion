package com.normalizar.repositoryDynamoDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.normalizar.repositoryDynamoDB.entity.MetaDataReport;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

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
            addAttributeIfNotNull(item, "tenant-name", metaDataReport.getTenantName());
            addAttributeIfNotNull(item, "lote-job-id", metaDataReport.getLoteJobId());
            addAttributeIfNotNull(item, "activo", metaDataReport.getActivo());
            addAttributeIfNotNull(item, "type", metaDataReport.getType());
            addAttributeIfNotNull(item, "user_pool_id", metaDataReport.getPoolUserId());
            addAttributeIfNotNull(item, "s3_json_path", metaDataReport.getS3JsonPath());
            addAttributeIfNotNull(item, "s3_pdf_path", metaDataReport.getS3PdfPath());
            addAttributeIfNotNull(item, "s3_zip_path", metaDataReport.getS3ZipPath());
            addAttributeIfNotNull(item, "s3_final_pdf_path", metaDataReport.getS3FinalPdfReport());
            addAttributeIfNotNull(item, "s3_final_zip_path", metaDataReport.getS3ZipFileFinalReport());
            addAttributeIfNotNull(item, "fecha_creacion", metaDataReport.getTimestamp());
            addAttributeIfNotNull(item, "estado", metaDataReport.getEstado());

            if (!item.containsKey("tenant-name") || !item.containsKey("lote-job-id")) {
                throw new IllegalArgumentException("la clave de particion 'tenant-name' y 'lote-job-id' no pueden ser nuls");
            }

            PutItemRequest putItemRequest = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(item)
                    .build();

            client.putItem(putItemRequest);
            System.out.println("Metadatos del reporte guardados exitosamente para tenant: "
                    + metaDataReport.getTenantName() + ", reporteId: " + metaDataReport.getLoteJobId());

            return "Metadatos del reporte guardados en DynamoDB para tenant: " + metaDataReport.getTenantName()
                    + ", reporteId: " + metaDataReport.getLoteJobId();

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

    private void addAttributeIfNotNull(Map<String, AttributeValue> item, String key, String value) {
        if (value != null && !value.isEmpty()) {
            item.put(key, AttributeValue.builder().s(value).build());
        }
    }

    @Override
    public void UpdateItemDbBrainStatus(String jobId, String tenantName, String status, DynamoDbClient client) {
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .tableName("report_SQS_brain")
                .key(Map.of(
                        "tenant-name", AttributeValue.builder().s(tenantName).build(),
                        "lote-job-id", AttributeValue.builder().s(jobId).build()))
                // Update expression
                .updateExpression("SET estado = :statusVal")
                .expressionAttributeValues(Map.of(
                        ":statusVal", AttributeValue.builder().s(status).build()))
                .build();

        client.updateItem(updateItemRequest);
    }

    @Override
    public void UpdateItemDbBrainStausPath(String jobId, String tenantName, String status, String pathS3Html,
            DynamoDbClient client) {
        UpdateItemRequest uoUpdateItemRequest = UpdateItemRequest.builder()
                .tableName("report_SQS_brain")
                .key(Map.of(
                        "tenant-name", AttributeValue.builder().s(tenantName).build(),
                        "lote-job-id", AttributeValue.builder().s(jobId).build()))
                .updateExpression("SET estado = :statusVal, path_HTML_S3 = :spath_html")
                .expressionAttributeValues(Map.of(":statusVal", AttributeValue.builder().s(status).build(),
                        ":spath_html", AttributeValue.builder().s(pathS3Html).build()))
                .build();
        client.updateItem(uoUpdateItemRequest);
    }

    @Override
    public List<Map<String, String>> consultarLotesPendientes(String tenantName, String jod_id, String status,
            DynamoDbClient client) {
        Map<String, String> expName = Map.of("#pk", "tenant-name",
                "#sk", "lote-job-id",
                "#status", "estado");

        Map<String, AttributeValue> expValues = Map.of(
                ":pkVal", AttributeValue.builder().s(tenantName).build(),
                ":skVal", AttributeValue.builder().s(jod_id).build(),
                ":estadoVal", AttributeValue.builder().s(status).build()

        );

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("report_SQS_brain")
                .keyConditionExpression("#pk = :pkVal AND begins_with(#sk, :skVal)")
                .filterExpression("#status = :estadoVal")
                .expressionAttributeNames(expName)
                .expressionAttributeValues(expValues)
                .build();

        QueryResponse queryResponse = client.query(queryRequest);

        List<Map<String, String>> resultado = new ArrayList<>();
        for (Map<String, AttributeValue> item : queryResponse.items()) {
            Map<String, String> itemPlano = new HashMap<>();
            item.forEach((k, v) -> {
                if (v.s() != null)
                    itemPlano.put(k, v.s());
                else if (v.n() != null)
                    itemPlano.put(k, v.n());
                else if (v.bool() != null)
                    itemPlano.put(k, v.bool().toString());
            });

            resultado.add(itemPlano);

        }

        return resultado;
    }

    @Override
    public List<Map<String, String>> consultarReportesPendientes(String tenantName, String poolUserId, String status,
            DynamoDbClient client) {
        Map<String, String> expName = Map.of("#pk", "tenant-name",
                "#poolId", "pool_user_id",
                "#status", "estado");

        Map<String, AttributeValue> expValues = Map.of(
                ":pkVal", AttributeValue.builder().s(tenantName).build(),
                ":poolIdVal", AttributeValue.builder().s(poolUserId).build(),
                ":estadoVal", AttributeValue.builder().s(status).build()

        );
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("report_SQS_brain")
                // Esta clase solo acepta una clave de partición en la KeyCondition
                //Puede haber condiciones sobre la SOT KEY
                .keyConditionExpression("#pk = :pkVal")
                // Otros atributos van aca para hacer la consulta
                .filterExpression("#poolId = :poolIdVal AND #status = :estadoVal")
                .expressionAttributeNames(expName)
                .expressionAttributeValues(expValues)
                .build();

        QueryResponse queryResponse = client.query(queryRequest);
        List<Map<String, String>> resultado = new ArrayList<>();
        for (Map<String, AttributeValue> item : queryResponse.items()) {
            Map<String, String> itemPlano = new HashMap<>();
            item.forEach((k, v) -> {
                if (v.s() != null)
                    itemPlano.put(k, v.s());
                else if (v.n() != null)
                    itemPlano.put(k, v.n());
                else if (v.bool() != null)
                    itemPlano.put(k, v.bool().toString());
            });

            resultado.add(itemPlano);

        }

        return resultado;
    }

}
