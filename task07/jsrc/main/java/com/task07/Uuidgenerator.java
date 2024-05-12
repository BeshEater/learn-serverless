package com.task07;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

@LambdaHandler(lambdaName = "uuid_generator",
	roleName = "UuidGenerator-role",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
public class Uuidgenerator implements RequestHandler<ScheduledEvent, String> {

	public String handleRequest(ScheduledEvent event, Context context) {
		System.out.println("Hello from lambda");

		return "Successfully handled scheduled event: " + event;
	}
}
