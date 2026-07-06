package com.selcukaloba.to_do_api_project.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{

    private final ErrorMessage errorMessage;

    public BaseException(ErrorMessage errorMessage)
    {
        super(errorMessage.generateErrorMessage());
        this.errorMessage = errorMessage;
    }
}
