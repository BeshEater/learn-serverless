package com.task05;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Event {
    private String id;
    private Integer principalId;
    private String createdAt;
    private Map<String, String> body;
}