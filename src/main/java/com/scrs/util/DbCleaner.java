package com.scrs.util;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class DbCleaner {
    public static void clearTable(DynamoDbClient client, String tableName, String... keyNames) {
        client.scan(ScanRequest.builder().tableName(tableName).build())
                .items()
                .forEach(item -> {
                    Map<String, AttributeValue> key = Map.of(
                            keyNames[0], item.get(keyNames[0]),
                            keyNames.length > 1 ? keyNames[1] : keyNames[0],
                            keyNames.length > 1 ? item.get(keyNames[1]) : item.get(keyNames[0])
                    );
                    client.deleteItem(DeleteItemRequest.builder()
                            .tableName(tableName)
                            .key(key)
                            .build());
                });
    }
}
