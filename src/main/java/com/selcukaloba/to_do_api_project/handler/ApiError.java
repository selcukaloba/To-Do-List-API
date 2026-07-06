package com.selcukaloba.to_do_api_project.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError<E> {
    private Integer status;
    private ExceptionDetails<E> exceptionDetails;
}
