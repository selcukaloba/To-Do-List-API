package com.selcukaloba.to_do_api_project.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionDetails<E> {
    private String path;
    private String errorCode;
    private String hostname;
    private Date errorTime;
    private E message;
}
