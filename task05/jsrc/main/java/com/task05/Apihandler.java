package com.task05;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(lambdaName = "api_handler",
	roleName = "ApiHandler-role",
	isPublishVersion = true,
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
public class Apihandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private final Gson gson = new Gson();
	private final String DYNAMO_DB_TABLE_NAME = "cmtr-e8ae546a-Events";

	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent gatewayEvent, Context context) {
		System.out.println("API gateway event = " + gatewayEvent.toString());
		var request = gson.fromJson(gatewayEvent.getBody(), Request.class);
		System.out.println("Request = " + request);
		System.out.println("Starting processing event, principalId = " + request.getPrincipalId());

		var event = Event.builder()
				.id(UUID.randomUUID().toString())
				.principalId(request.getPrincipalId())
				.createdAt(OffsetDateTime.now().format(DateTimeFormatter.ISO_INSTANT))
				.body(request.getContent())
				.build();

		System.out.println("EVENT = " + event);

		System.out.println("Trying to create DynamoDb client:");
		var dynamoDbClient = DynamoDbEnhancedClient.create();
		var dynamoDbTable = dynamoDbClient.table(DYNAMO_DB_TABLE_NAME, TableSchema.fromBean(Event.class));
		System.out.println("DynamoDb client created");

		System.out.println("Putting new item in DynamoDB");
		dynamoDbTable.putItem(event);
		System.out.println("Finished putting new item in DynamoDB");

		var response = Response.builder()
				.statusCode(201)
				.event(event)
				.build();

		System.out.println("Response = " + response);

		var apiGatewayResponse = new APIGatewayProxyResponseEvent();
		apiGatewayResponse.setStatusCode(201);
		apiGatewayResponse.setBody(gson.toJson(response));
		apiGatewayResponse.setHeaders(Map.of("Content-Type", "application/json"));
		return apiGatewayResponse;
	}
}