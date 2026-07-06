package com.selcukaloba.to_do_api_project.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionDetails<E> {
    private String path;
    private String hostname;
    private Date errorTime;
    private E message;
}
