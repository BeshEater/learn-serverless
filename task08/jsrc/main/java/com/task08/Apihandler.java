package com.task08;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@LambdaHandler(lambdaName = "api_handler",
	roleName = "ApiHandler-role",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig
public class Apihandler implements RequestHandler<Object, String> {

	public String handleRequest(Object request, Context context) {
		var currentWeatherRequest = HttpRequest.newBuilder()
				.uri(URI.create("https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,wind_speed_10m"))
				.GET()
				.build();

		HttpResponse<String> response;
		try {
			response = HttpClient.newBuilder()
					.build()
					.send(currentWeatherRequest, HttpResponse.BodyHandlers.ofString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return response.body();
	}
}
