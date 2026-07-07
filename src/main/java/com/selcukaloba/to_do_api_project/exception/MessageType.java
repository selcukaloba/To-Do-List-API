package com.selcukaloba.to_do_api_project.exception;

import lombok.Getter;

@Getter
public enum MessageType {
    NO_RECORD_EXISTS("1001", "No such record!"),
    USERNAME_NOT_FOUND("1002", "No such user!"),
    GENERAL_EXCEPTION("9999", "General exception!");

    private String code, message;

    MessageType(String code, String message)
    {
        this.code = code;
        this.message = message;
    }
}
