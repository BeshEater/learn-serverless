package com.task06;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class ExistingItemModifiedAuditEntry {
    private String id;
    private String itemKey;
    private String modificationTime;
    private String updatedAttribute;
    private Long oldValue;
    private Long newValue;

    @DynamoDbPartitionKey
    public String getId() {
        return this.id;
    }
}
