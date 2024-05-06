package com.LambdaQueue;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.events.SnsEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

@LambdaHandler(lambdaName = "sns_handler",
	roleName = "SnsHandler-role",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@SnsEventSource(targetTopic = "lambda_topic")
public class Snshandler implements RequestHandler<Object, String> {

	public String handleRequest(Object event, Context context) {
		System.out.println("SNS event: " + event);

		return "Successfully logged even data from SNS";
	}
}
