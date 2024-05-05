package com.LambdaQueue;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syndicate.deployment.annotations.events.SqsTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

@LambdaHandler(lambdaName = "sqs_handler-test",
	roleName = "SqsHandler-role",
	isPublishVersion = true,
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@SqsTriggerEventSource(targetQueue = "async_queue-test", batchSize = 2)
public class Sqshandler implements RequestHandler<Object, String> {

	Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public String handleRequest(Object event, Context context) {
		LambdaLogger logger = context.getLogger();
		// log execution details
		logger.log("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()));
		logger.log("CONTEXT: " + gson.toJson(context));
		// process event
		logger.log("EVENT: " + gson.toJson(event));

		return "Successfully logged even data from SQS";
	}
}
