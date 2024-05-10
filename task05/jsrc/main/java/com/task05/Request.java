package com.task05;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class Request {
    private Integer principalId;
    private Map<String, String> content;
}
