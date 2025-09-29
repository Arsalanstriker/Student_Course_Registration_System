package com.scrs.util;

import com.scrs.config.DynamoDbConfig;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.List;

public class TableInitializer {
    private static final DynamoDbClient client = DynamoDbConfig.createClient();

    public static void main(String[] args) {
        createTableIfNotExists("Students", "studentId");
        createTableIfNotExists("Courses", "courseId");
        createTableIfNotExists("Enrollments", "studentId", "courseId");
    }

    // 👉 This makes sure we don’t rebuild tables every run (faster startup)
    private static void createTableIfNotExists(String tableName, String... keys) {
        try {
            ListTablesResponse tables = client.listTables();
            if (tables.tableNames().contains(tableName)) {
                System.out.println("Table exists: " + tableName);
                return;
            }

            CreateTableRequest.Builder request = CreateTableRequest.builder()
                    .tableName(tableName)
                    .billingMode(BillingMode.PAY_PER_REQUEST);

            if (keys.length == 1) {
                request.keySchema(KeySchemaElement.builder()
                        .attributeName(keys[0]).keyType(KeyType.HASH).build());
                request.attributeDefinitions(AttributeDefinition.builder()
                        .attributeName(keys[0]).attributeType(ScalarAttributeType.S).build());
            } else if (keys.length == 2) {
                request.keySchema(
                        KeySchemaElement.builder().attributeName(keys[0]).keyType(KeyType.HASH).build(),
                        KeySchemaElement.builder().attributeName(keys[1]).keyType(KeyType.RANGE).build()
                );
                request.attributeDefinitions(
                        AttributeDefinition.builder().attributeName(keys[0]).attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder().attributeName(keys[1]).attributeType(ScalarAttributeType.S).build()
                );
            }

            client.createTable(request.build());
            System.out.println("Table created: " + tableName);
        } catch (Exception e) {
            System.err.println("Error creating table " + tableName + ": " + e.getMessage());
        }
    }
}
