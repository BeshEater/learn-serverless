package com.task10;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.task10.table.TableDbEntry;
import com.task10.table.TablePostRequest;
import com.task10.table.TablePostResponse;
import com.task10.table.TablesGetResponse;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.stream.Collectors;

public class TableService {
    private final Gson gson = new Gson();

    public APIGatewayProxyResponseEvent handleGetRequest(APIGatewayProxyRequestEvent requestEvent) {
        var dynamoDbTable = getDynamoDbTable();
        var tableDbEntries = dynamoDbTable.scan()
                .items()
                .stream()
                .collect(Collectors.toList());

        var response = TablesGetResponse.from(tableDbEntries);
        System.out.println("TablesGetResponse = " + response);

        return Utils.createSuccessfulResponseEvent(response);
    }

    public APIGatewayProxyResponseEvent handlePostRequest(APIGatewayProxyRequestEvent requestEvent) {
        var request = gson.fromJson(requestEvent.getBody(), TablePostRequest.class);
        System.out.println("TablePostRequest = " + request);

        var tableDbEntry = TableDbEntry.from(request);
        System.out.println("TableDbEntry = " + tableDbEntry);
        var dynamoDbTable = getDynamoDbTable();
        dynamoDbTable.putItem(tableDbEntry);

        var response = new TablePostResponse(tableDbEntry.getId());
        System.out.println("TablePostResponse = " + response);

        return Utils.createSuccessfulResponseEvent(response);
    }

    private DynamoDbTable<TableDbEntry> getDynamoDbTable() {
        return Utils.getDynamoDbTable("Tables", TableDbEntry.class);
    }
}
