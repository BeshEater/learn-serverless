package com.task05;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Response {
    private Integer statusCode;
    private Event event;
}
