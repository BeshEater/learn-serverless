package com.task05;

import lombok.Data;

import java.util.Map;

@Data
public class Request {
    private Integer principalId;
    private Map<String, String> content;
}
