package com.task07;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.google.gson.Gson;
import com.syndicate.deployment.annotations.events.RuleEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

@LambdaHandler(lambdaName = "uuid_generator",
	roleName = "UuidGenerator-role",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@RuleEventSource(targetRule = "uuid_trigger")
public class Uuidgenerator implements RequestHandler<ScheduledEvent, String> {

	public String handleRequest(ScheduledEvent event, Context context) {
		System.out.println("Handling event = " + event);

		var fileName = OffsetDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
		var fileContent = generateFileContent();

		System.out.println("File name = " + fileName);
		System.out.println("File content = " + fileContent);

		try (var s3Client = S3Client.create()) {
			var putObjectRequest = PutObjectRequest.builder()
					.bucket(getBucketName(context.getFunctionName()))
					.key(fileName)
					.build();
			var requestBody = RequestBody.fromString(fileContent);
			System.out.println("PutObjectRequest = " + putObjectRequest);
			System.out.println("RequestBody = " + requestBody);

			System.out.println("Tyring to upload file to s3 bucket");
			s3Client.putObject(putObjectRequest, requestBody);
			System.out.println("Finished uploading file to s3 bucket");
		}

		return "Successfully handled scheduled event: " + event;
	}

	private String generateFileContent() {
		var ids = new ArrayList<String>(10);
		for (int i = 0; i < 10; i++) {
			ids.add(UUID.randomUUID().toString());
		}
		var fileContent = new FileContent(ids);
		var gson = new Gson();
		return gson.toJson(fileContent);
	}

	private String getBucketName(String functionName) {
		return functionName.replace("uuid_generator", "uuid-storage");
	}
}
