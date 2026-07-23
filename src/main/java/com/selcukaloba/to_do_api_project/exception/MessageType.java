package com.selcukaloba.to_do_api_project.exception;

import lombok.Getter;

@Getter
public enum MessageType {
    //validation errors
    VALIDATION_ERROR("1000", "validation.error"),

    //authentication errors
    NO_RECORD_EXISTS("1001", "user.no_record_exists"),
    USERNAME_NOT_FOUND("1002", "user.not_found"),
    USER_ALREADY_EXISTS("1003", "user.already_exists"),

    //todos errors
    INVALID_DAY_RANGE("2001", "todo.invalid_day_range"),
    INVALID_DATE_ORDER("2002", "todo.invalid_date_order"),
    TODO_NOT_FOUND("2003", "todo.not_found"),
    NOT_TODO_OWNER("2004", "todo.not_owner"),

    //friendship and share errors
    FRIEND_NOT_FOUND("3001", "friend.not_found"),
    NOT_FRIENDS("3002", "share.not_friends"),
    ALREADY_SHARED("3003", "share.already_shared"),
    SHARE_REQUEST_PENDING("3004", "share.request_pending"),
    SHARE_REQUEST_NOT_FOUND("3005", "share.request_not_found"),

    GENERAL_EXCEPTION("9999", "general.exception");

    private String code, message;

    MessageType(String code, String message)
    {
        this.code = code;
        this.message = message;
    }
}
