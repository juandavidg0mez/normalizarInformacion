package com.normalizar.repositoryDynamoDB;

import java.util.List;
import java.util.Map;

import com.normalizar.repositoryDynamoDB.entity.MetaDataReport;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;


public interface IuseCaseDynamoDB {
    String CreateItem(MetaDataReport metaDataReport, DynamoDbClient client, String tableName);
    String CreateItem(String tenant, String poolUserId);
    void UpdateItemDbBrainStatus(String jobId , String tenantName, String status, DynamoDbClient client);
    void UpdateItemDbBrainStausPath (String jobId , String tenantName, String status, String pathS3Html,DynamoDbClient client);
    List<Map<String, String>> consultarLotesPendientes(String tenantName, String jod_id, String status, DynamoDbClient client);
    List<Map<String, String>> consultarReportesPendientes(String tenantName, String poolUserId, String status, DynamoDbClient client);
}
