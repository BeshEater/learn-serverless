package com.task05;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response {
    private Integer statusCode;
    private Event event;
}
