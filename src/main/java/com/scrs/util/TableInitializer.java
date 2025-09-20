package com.scrs.util;

import com.scrs.config.DynamoDbConfig;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.List;

public class TableInitializer {

    private static final DynamoDbClient client = DynamoDbConfig.createClient();

    public static void main(String[] args) {
        createStudentsTable();
        createCoursesTable();
        createEnrollmentsTable();

        System.out.println("✅ Tables created/verified successfully!");
    }

    private static void createStudentsTable() {
        String tableName = "Students";
        if (tableExists(tableName)) {
            System.out.println("ℹ️ Students table already exists.");
            return;
        }

        client.createTable(CreateTableRequest.builder()
                .tableName(tableName)
                .attributeDefinitions(
                        AttributeDefinition.builder().attributeName("studentId").attributeType(ScalarAttributeType.S).build()
                )
                .keySchema(
                        KeySchemaElement.builder().attributeName("studentId").keyType(KeyType.HASH).build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build());

        System.out.println("✅ Students table created.");
    }

    private static void createCoursesTable() {
        String tableName = "Courses";
        if (tableExists(tableName)) {
            System.out.println("ℹ️ Courses table already exists.");
            return;
        }

        client.createTable(CreateTableRequest.builder()
                .tableName(tableName)
                .attributeDefinitions(
                        AttributeDefinition.builder().attributeName("courseId").attributeType(ScalarAttributeType.S).build()
                )
                .keySchema(
                        KeySchemaElement.builder().attributeName("courseId").keyType(KeyType.HASH).build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build());

        System.out.println("✅ Courses table created.");
    }

    private static void createEnrollmentsTable() {
        String tableName = "Enrollments";
        if (tableExists(tableName)) {
            System.out.println("ℹ️ Enrollments table already exists.");
            return;
        }

        client.createTable(CreateTableRequest.builder()
                .tableName(tableName)
                .attributeDefinitions(
                        AttributeDefinition.builder().attributeName("studentId").attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder().attributeName("courseId").attributeType(ScalarAttributeType.S).build()
                )
                .keySchema(
                        KeySchemaElement.builder().attributeName("studentId").keyType(KeyType.HASH).build(),
                        KeySchemaElement.builder().attributeName("courseId").keyType(KeyType.RANGE).build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build());

        System.out.println("✅ Enrollments table created.");
    }

    private static boolean tableExists(String tableName) {
        ListTablesResponse tables = client.listTables();
        return tables.tableNames().contains(tableName);
    }
}
