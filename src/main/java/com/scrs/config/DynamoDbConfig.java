package com.scrs.config;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
//DynamoDb configuration through Javasdk
public class DynamoDbConfig {
    public static DynamoDbClient createClient() {
        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        //dummy credentials
                        AwsBasicCredentials.create("dummy", "dummy")))
                //Hosting on 8001 port
                .endpointOverride(URI.create("http://localhost:8005"))
                .build();
    }
}
