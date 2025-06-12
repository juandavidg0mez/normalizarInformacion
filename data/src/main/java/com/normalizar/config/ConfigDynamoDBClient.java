package com.normalizar.config;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDBClient;

public class ConfigDynamoDBClient {
    public static DynamoDBClient clientDbClient(){
        return  DynamoDBClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }
}
