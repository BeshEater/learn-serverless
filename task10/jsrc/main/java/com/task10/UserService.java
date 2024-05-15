package com.task10;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.task10.user.UserSignInRequest;
import com.task10.user.UserSignInResponse;
import com.task10.user.UserSignupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolsRequest;

import java.util.Map;

public class UserService {
    private final Gson gson = new Gson();

    public APIGatewayProxyResponseEvent handleSignupRequest(APIGatewayProxyRequestEvent requestEvent) {
        var request = gson.fromJson(requestEvent.getBody(), UserSignupRequest.class);
        System.out.println("UserSignupRequest = " + request);

        var cognitoClient = CognitoIdentityProviderClient.create();
        var userPoolId = getUserPoolId(cognitoClient);
        try {
            var userAttrs = AttributeType.builder()
                    .name("email")
                    .value(request.getEmail())
                    .build();

            var userRequest = AdminCreateUserRequest.builder()
                    .userPoolId(userPoolId)
                    .temporaryPassword(request.getPassword())
                    .username(request.getEmail())
//                    .userAttributes(userAttrs)
                    .messageAction("SUPPRESS")
                    .build();

            var response = cognitoClient.adminCreateUser(userRequest);
            System.out.println("User " + response.user().username() + "is created. Status: " + response.user().userStatus());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Utils.createSuccessfulResponseEvent();
    }

    public APIGatewayProxyResponseEvent handleSigninRequest(APIGatewayProxyRequestEvent requestEvent) {
        var request = gson.fromJson(requestEvent.getBody(), UserSignInRequest.class);
        System.out.println("UserSignupRequest = " + request);

        var cognitoClient = CognitoIdentityProviderClient.create();
        var userPoolId = getUserPoolId(cognitoClient);

        var authParams = Map.of(
                "USERNAME", request.getEmail(),
                "PASSWORD", request.getPassword()
        );

        var authRequest = AdminInitiateAuthRequest.builder()
                .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .userPoolId(userPoolId)
                .authParameters(authParams)
                .build();

        var authResponse = cognitoClient.adminInitiateAuth(authRequest);

        System.out.println("Auth response: " + authResponse);

        System.out.println("ID Token: " + authResponse.authenticationResult().idToken());
        System.out.println("Access Token: " + authResponse.authenticationResult().accessToken());
        System.out.println("Refresh Token: " + authResponse.authenticationResult().refreshToken());

        var response = new UserSignInResponse(authResponse.authenticationResult().accessToken());

        return Utils.createSuccessfulResponseEvent(response);
    }

    private String getUserPoolId(CognitoIdentityProviderClient cognitoClient) {
        var userPoolName = Utils.getResourceNameWithPrefixAndSuffix("simple-booking-userpool");
        String userPoolId = "";
        try {
            var request = ListUserPoolsRequest.builder()
                    .maxResults(60)
                    .build();

            var response = cognitoClient.listUserPools(request);
            response.userPools().forEach(userpool -> {
                System.out.println("User pool " + userpool.name() + ", User ID " + userpool.id());
            });
            userPoolId = response.userPools().stream()
                    .filter(userPool -> userPoolName.equals(userPool.name()))
                    .findFirst()
                    .orElseThrow()
                    .id();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("UserPoolId = " + userPoolId);

        return userPoolId;
    }
}