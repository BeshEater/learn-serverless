package com.task10;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

@LambdaHandler(lambdaName = "api_handler",
        roleName = "ApiHandler-role",
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
public class Apihandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Gson gson = new Gson();
	public static volatile String FUNCTION_NAME = "";
	private final TableService tableService = new TableService();
	public final ReservationService reservationService = new ReservationService();

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent gatewayEvent, Context context) {
		FUNCTION_NAME = context.getFunctionName();
        System.out.println("API gateway event = " + gatewayEvent.toString());

		APIGatewayProxyResponseEvent responseEvent = null;
		if (gatewayEvent.getPath().equals("/tables")) {
			if (gatewayEvent.getHttpMethod().equals("GET")) {
				responseEvent = tableService.handleGetRequest(gatewayEvent);
			}
			if (gatewayEvent.getHttpMethod().equals("POST")) {
				responseEvent = tableService.handlePostRequest(gatewayEvent);
			}
		}
		if (gatewayEvent.getPath().equals("/reservations")) {
			if (gatewayEvent.getHttpMethod().equals("GET")) {
				responseEvent = reservationService.handleGetRequest(gatewayEvent);
			}
			if (gatewayEvent.getHttpMethod().equals("POST")) {
				responseEvent = reservationService.handlePostRequest(gatewayEvent);
			}
		}
        return responseEvent;
    }
}
