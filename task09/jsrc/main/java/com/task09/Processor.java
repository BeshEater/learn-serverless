package com.task09;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(lambdaName = "processor",
	roleName = "Processor-role",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig
public class Processor implements RequestHandler<Object, String> {

	public String handleRequest(Object request, Context context) {
		System.out.println("Handling request: " + request);

		var response = OpenMeteoClient.getCurrentWeather();
		System.out.println("Weather response = " + response);

		return response;
	}
}
