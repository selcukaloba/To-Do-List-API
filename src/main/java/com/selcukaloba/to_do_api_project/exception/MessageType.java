package com.selcukaloba.to_do_api_project.exception;

import lombok.Getter;

@Getter
public enum MessageType {
    //validation errors
    VALIDATION_ERROR("1000", "Input validation failed!"),

    //authentication errors
    NO_RECORD_EXISTS("1001", "No such record!"),
    USERNAME_NOT_FOUND("1002", "No such user!"),
    USER_ALREADY_EXISTS("1003", "User already exists!"),

    //todos errors
    INVALID_DAY_RANGE("2001", "Invalid day range!"),
    INVALID_DATE_ORDER("2002", "Reminder date cannot be after than due date!"),
    TODO_NOT_FOUND("2003", "Todo could not be found!"),
    NOT_TODO_OWNER("2004", "You are not owner of the Todo!"),

    //friendship and share errors
    FRIEND_NOT_FOUND("3001", "Friend could not be found!"),
    NOT_FRIENDS("3002", "Share is only can be done with friends!"),
    ALREADY_SHARED("3003", "This todo already shared!"),
    SHARE_REQUEST_PENDING("3004", "Share request already pending!"),
    SHARE_REQUEST_NOT_FOUND("3005", "Share request could not be found!"),

    GENERAL_EXCEPTION("9999", "General exception!");

    private String code, message;

    MessageType(String code, String message)
    {
        this.code = code;
        this.message = message;
    }
}
