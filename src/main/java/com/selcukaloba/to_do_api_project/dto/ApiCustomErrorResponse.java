package com.selcukaloba.to_do_api_project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiCustomErrorResponse {
    private String path;
    private String errorCode;
    private String message;
    private LocalDateTime timeStamp;
    private Map<String, String> field;
}
