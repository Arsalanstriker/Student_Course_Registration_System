package com.scrs.util;

import com.scrs.config.AppConfig;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbCleaner {
    public static void clearTable(DynamoDbClient client, String tableName, String... keyNames) {
        ScanResponse scan = client.scan(ScanRequest.builder().tableName(tableName).build());
        List<Map<String, AttributeValue>> items = scan.items();

        for (Map<String, AttributeValue> item : items) {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put(keyNames[0], item.get(keyNames[0]));
            if (keyNames.length > 1) {
                key.put(keyNames[1], item.get(keyNames[1]));
            }
            client.deleteItem(DeleteItemRequest.builder()
                    .tableName(tableName)
                    .key(key)
                    .build());
        }
    }
}
