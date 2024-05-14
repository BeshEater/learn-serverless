package com.task09;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.TracingMode;
import com.task09.dynamodb.Forecast;
import com.task09.dynamodb.Weather;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.UUID;

@LambdaHandler(lambdaName = "processor",
	roleName = "Processor-role",
	tracingMode = TracingMode.Active,
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig
public class Processor implements RequestHandler<Object, String> {

	private DynamoDbEnhancedClient dynamoDbClient = DynamoDbEnhancedClient.create();

	public String handleRequest(Object request, Context context) {
		System.out.println("Handling request: " + request);

		var response = OpenMeteoClient.getCurrentWeather();
		System.out.println("Weather response = " + response);

		var gson = new Gson();
		var forecast = gson.fromJson(response, Forecast.class);
		var weather = Weather.builder()
				.id(UUID.randomUUID().toString())
				.forecast(forecast)
				.build();
		System.out.println("Weather object for DynamoDB = " + weather);

		System.out.println("Trying to create DynamoDb client:");
		var tableName = getDynamoDbTableName(context.getFunctionName());
		System.out.println("Function name = " + context.getFunctionName() + " | table name = " + tableName);

		var dynamoDbTable = dynamoDbClient.table(tableName, TableSchema.fromBean(Weather.class));
		dynamoDbTable.putItem(weather);

		return "Successfully processed request: " + request;
	}

	private String getDynamoDbTableName(String functionName) {
		return functionName.replace("processor", "Weather");
	}
}
