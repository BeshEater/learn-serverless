package com.task10;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.task10.reservation.ReservationDbEntry;
import com.task10.reservation.ReservationPostRequest;
import com.task10.reservation.ReservationPostResponse;
import com.task10.reservation.ReservationsGetResponse;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.stream.Collectors;

public class ReservationService {
    private final Gson gson = new Gson();

    public APIGatewayProxyResponseEvent handleGetRequest(APIGatewayProxyRequestEvent requestEvent) {
        var dynamoDbTable = getDynamoDbTable();
        var tableDbEntries = dynamoDbTable.scan()
                .items()
                .stream()
                .collect(Collectors.toList());

        var response = ReservationsGetResponse.from(tableDbEntries);
        System.out.println("ReservationsGetResponse = " + response);

        return Utils.createSuccessfulResponseEvent(response);
    }

    public APIGatewayProxyResponseEvent handlePostRequest(APIGatewayProxyRequestEvent requestEvent) {
        var request = gson.fromJson(requestEvent.getBody(), ReservationPostRequest.class);
        System.out.println("ReservationPostRequest = " + request);

        var reservationDbEntry = ReservationDbEntry.from(request);
        System.out.println("ReservationDbEntry = " + reservationDbEntry);
        var dynamoDbTable = getDynamoDbTable();
        dynamoDbTable.putItem(reservationDbEntry);

        var response = new ReservationPostResponse(reservationDbEntry.getReservationId());
        System.out.println("ReservationPostResponse = " + response);

        return Utils.createSuccessfulResponseEvent(response);
    }

    private DynamoDbTable<ReservationDbEntry> getDynamoDbTable() {
        return Utils.getDynamoDbTable("Reservations", ReservationDbEntry.class);
    }
}
