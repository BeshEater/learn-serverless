package com.task10.services;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.task10.Utils;
import com.task10.dto.reservation.ReservationDbEntry;
import com.task10.dto.reservation.ReservationPostRequest;
import com.task10.dto.reservation.ReservationPostResponse;
import com.task10.dto.reservation.ReservationsGetResponse;
import com.task10.repositories.ReservationRepository;

public class ReservationService {
    private final Gson gson = new Gson();
    private final ReservationRepository reservationRepository = new ReservationRepository();

    public APIGatewayProxyResponseEvent handleListReservationsRequest() {
        var tableDbEntries = reservationRepository.listReservations();

        var response = ReservationsGetResponse.from(tableDbEntries);
        System.out.println("ReservationsGetResponse = " + response);

        return Utils.createSuccessfulResponseEvent(response);
    }

    public APIGatewayProxyResponseEvent handleCreateReservationRequest(APIGatewayProxyRequestEvent requestEvent) {
        var request = gson.fromJson(requestEvent.getBody(), ReservationPostRequest.class);
        System.out.println("ReservationPostRequest = " + request);

        var reservationDbEntry = ReservationDbEntry.from(request);
        System.out.println("ReservationDbEntry = " + reservationDbEntry);
        reservationRepository.saveReservation(reservationDbEntry);

        var response = new ReservationPostResponse(reservationDbEntry.getReservationId());
        System.out.println("ReservationPostResponse = " + response);

        return Utils.createSuccessfulResponseEvent(response);
    }
}
