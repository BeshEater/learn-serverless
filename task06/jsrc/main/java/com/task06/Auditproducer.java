package com.task06;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.StreamRecord;
import com.syndicate.deployment.annotations.events.DynamoDbTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@LambdaHandler(lambdaName = "audit_producer",
	roleName = "AuditProducer-role",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@DynamoDbTriggerEventSource(targetTable = "Configuration", batchSize = 1)
public class Auditproducer implements RequestHandler<DynamodbEvent, String> {

	private DynamoDbEnhancedClient dynamoDbClient = DynamoDbEnhancedClient.create();

	public String handleRequest(DynamodbEvent dynamodbEvent, Context context) {
		System.out.println("Dynamo db event = " + dynamodbEvent);
		System.out.println("Dynamo db records size = " + dynamodbEvent.getRecords().size());
		var dynamoDbRecord = dynamodbEvent.getRecords().get(0);
		System.out.println("Dynamo db record = " + dynamoDbRecord);
		var streamRecord = dynamoDbRecord.getDynamodb();
		System.out.println("Dynamo db stream record = " + streamRecord);

		System.out.println("Trying to create DynamoDb client:");
		var tableName = getDynamoDbTableName(context.getFunctionName());
		System.out.println("Function name = " + context.getFunctionName() + " | table name = " + tableName);
		if (dynamoDbRecord.getEventName().equals("INSERT")) {
			this.saveNewItemAddedAuditEntry(tableName, streamRecord);
		}
		if (dynamoDbRecord.getEventName().equals("MODIFY")) {
			this.saveExistingItemModifiedAuditEntry(tableName, streamRecord);
		}

		return "Successfully processed event: " + dynamodbEvent;
	}

	private void saveNewItemAddedAuditEntry(String tableName, StreamRecord streamRecord) {
		var newImage = streamRecord.getNewImage();
		System.out.println("New Image = " + newImage);
		var key = newImage.get("key").getS();
		var value = Long.valueOf(newImage.get("value").getN());

		var newItemAddedAuditEntry = NewItemAddedAuditEntry.builder()
				.id(UUID.randomUUID().toString())
				.itemKey(key)
				.modificationTime(OffsetDateTime.now().format(DateTimeFormatter.ISO_INSTANT))
				.newValue(NewItemAddedAuditEntry.NewValue.builder()
						.key(key)
						.value(value)
						.build()
				)
				.build();

		System.out.println("newItemAddedAuditEntry = " + newItemAddedAuditEntry);

		var dynamoDbTable = dynamoDbClient.table(tableName, TableSchema.fromBean(NewItemAddedAuditEntry.class));
		dynamoDbTable.putItem(newItemAddedAuditEntry);
	}

	private void saveExistingItemModifiedAuditEntry(String tableName, StreamRecord streamRecord) {
		var oldImage = streamRecord.getNewImage();
		var newImage = streamRecord.getNewImage();
		System.out.println("Old Image = " + oldImage);
		System.out.println("New Image = " + newImage);
		var key = newImage.get("key").getS();
		var oldValue = Long.valueOf(oldImage.get("value").getN());
		var newValue = Long.valueOf(newImage.get("value").getN());

		var existingItemModifiedAuditEntry = ExistingItemModifiedAuditEntry.builder()
				.id(UUID.randomUUID().toString())
				.itemKey(key)
				.modificationTime(OffsetDateTime.now().format(DateTimeFormatter.ISO_INSTANT))
				.updatedAttribute("value")
				.oldValue(oldValue)
				.newValue(newValue)
				.build();

		System.out.println("existingItemModifiedAuditEntry = " + existingItemModifiedAuditEntry);

		var dynamoDbTable = dynamoDbClient.table(tableName, TableSchema.fromBean(ExistingItemModifiedAuditEntry.class));
		dynamoDbTable.putItem(existingItemModifiedAuditEntry);
	}

	private String getDynamoDbTableName(String functionName) {
		return functionName.replace("audit_producer", "Audit");
	}
}