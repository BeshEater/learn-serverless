package com.LearnTaskHelloWorldLambdaFunction5;

import lombok.Data;

import java.util.Map;

@Data
public class LambdaResponse {
    Integer statusCode = 200;
    Map<String, Object> body;
    Map<String, Object> headers;
    Boolean isBase64Encoded = false;
}
