package com.LambdaQueue;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.events.SqsTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

@LambdaHandler(lambdaName = "sqs_handler",
	roleName = "SqsHandler-role",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@SqsTriggerEventSource(targetQueue = "async_queue", batchSize = 2)
public class Sqshandler implements RequestHandler<Object, String> {

	public String handleRequest(Object event, Context context) {
		System.out.println("SQS event: " + event);

		return "Successfully logged even data from SQS";
	}
}
