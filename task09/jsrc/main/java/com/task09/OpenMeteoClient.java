package com.task09;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OpenMeteoClient {

    public static String getCurrentWeather() {
        var currentWeatherRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&hourly=temperature_2m"))
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
