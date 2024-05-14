package com.task08;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.Architecture;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.RetentionSetting;

@LambdaHandler(lambdaName = "api_handler",
	roleName = "ApiHandler-role",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaLayer(
		layerName = "open-meteo-layer",
		libraries = {"lib/gson-2.10.1.jar"},
		runtime = DeploymentRuntime.JAVA11,
		architectures = {Architecture.ARM64},
		artifactExtension = ArtifactExtension.ZIP
)

@LambdaUrlConfig
public class Apihandler implements RequestHandler<Object, String> {

	public String handleRequest(Object request, Context context) {
		System.out.println("Handling request: " + request);

		var gson = new Gson();
		System.out.println("Gson = " + gson);

		var response = OpenMeteoClient.getCurrentWeather();
		System.out.println("Weater response = " + response);

		return response;
	}
}
