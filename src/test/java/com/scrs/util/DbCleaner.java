package com.scrs.util;

import com.scrs.config.DynamoDbConfig;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

public class DbCleaner {
    private static final DynamoDbClient client = DynamoDbConfig.createClient();

    public static void clearTable(String tableName, String pkName, String skName) {
        var scan = client.scan(ScanRequest.builder().tableName(tableName).build());
        for (Map<String, AttributeValue> item : scan.items()) {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put(pkName, item.get(pkName));
            if (skName != null && item.containsKey(skName)) {
                key.put(skName, item.get(skName));
            }
            client.deleteItem(DeleteItemRequest.builder().tableName(tableName).key(key).build());
        }
    }
}
