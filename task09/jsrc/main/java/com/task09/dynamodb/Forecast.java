package com.task09.dynamodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Forecast {
    private Double elevation;
    private Double generationtime_ms;
    private Hourly hourly;
    private HourlyUnits hourly_units;
    private Double latitude;
    private Double longitude;
    private String timezone;
    private String timezone_abbreviation;
    private Long utc_offset_seconds;
}
