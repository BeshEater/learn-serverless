package com.LearnTaskHelloWorldLambdaFunction5;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(lambdaName = "hello_world-test",
	roleName = "TestLambda-role",
	isPublishVersion = true,
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig
public class Testlambda implements RequestHandler<Object, LambdaResponse> {

	public LambdaResponse handleRequest(Object request, Context context) {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("statusCode", 200);
		body.put("message", "Hello from Lambda");
		LambdaResponse response = new LambdaResponse();
		response.body = body;
		return response;
	}
}
