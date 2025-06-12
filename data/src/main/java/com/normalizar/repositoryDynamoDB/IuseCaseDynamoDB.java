package com.normalizar.repositoryDynamoDB;




import com.normalizar.repositoryDynamoDB.entity.MetaDataReport;

import software.amazon.awssdk.services.dynamodb.DynamoDBClient;



public interface IuseCaseDynamoDB {
    String CreateItem(MetaDataReport metaDataReport, DynamoDBClient client, String tableName);
    String CreateItem(String tenant, String poolUserId);
}
