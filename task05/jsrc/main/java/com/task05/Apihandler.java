package com.task05;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@LambdaHandler(lambdaName = "api_handler",
	roleName = "ApiHandler-role",
	isPublishVersion = true,
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
public class Apihandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

	private final Gson gson = new Gson();

	public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent gatewayEvent, Context context) {
		var request = gson.fromJson(gatewayEvent.getBody(), Request.class);
		System.out.println("Starting processing event, principalId = " + request.getPrincipalId());

		var event = Event.builder()
				.id(UUID.randomUUID().toString())
				.principalId(request.getPrincipalId())
				.createdAt(OffsetDateTime.now().format(DateTimeFormatter.ISO_INSTANT))
				.body(request.getContent())
				.build();

		var response = Response.builder()
				.statusCode(201)
				.event(event)
				.build();

		return APIGatewayV2HTTPResponse.builder()
				.withStatusCode(201)
				.withBody(gson.toJson(response))
				.build();
	}
}
