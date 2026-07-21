package com.selcukaloba.to_do_api_project.handler;

import com.selcukaloba.to_do_api_project.exception.BaseException;
import com.selcukaloba.to_do_api_project.exception.ErrorMessage;
import com.selcukaloba.to_do_api_project.exception.MessageType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    //service errors
    @ExceptionHandler(value = {BaseException.class})
    public ResponseEntity<ApiError<?>> handleBaseException(BaseException ex, WebRequest request)
    {
        ErrorMessage errorMessage = ex.getErrorMessage();
        String code = errorMessage.getMessageType().getCode();
        String detailMessage = errorMessage.generateErrorMessage();

        return ResponseEntity.badRequest().body(createApiError(HttpStatus.BAD_REQUEST, code, detailMessage, request));
    }

    //validation errors
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ApiError<Map<String, List<String>>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request)
    {
        Map<String, List<String>> map = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String fieldName = fieldError.getField();
            map.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(fieldError.getDefaultMessage());
        }

        for (ObjectError objectError : ex.getBindingResult().getGlobalErrors()) {
            String objectName = objectError.getObjectName();
            map.computeIfAbsent(objectName, k -> new ArrayList<>()).add(objectError.getDefaultMessage());
        }

        String validationCode = MessageType.VALIDATION_ERROR.getCode();

        return ResponseEntity
                .badRequest()
                .body(createApiError(HttpStatus.BAD_REQUEST, validationCode, map, request));
    }

    //other errors
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ApiError<String>> handleGeneralException(Exception ex, WebRequest request)
    {
        String generalCode = MessageType.GENERAL_EXCEPTION.getCode();
        String message = ex.getMessage() != null ? ex.getMessage() : MessageType.GENERAL_EXCEPTION.getMessage();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createApiError(HttpStatus.INTERNAL_SERVER_ERROR, generalCode, message, request));
    }

    private String getHostName() {
        try {
            return Inet4Address.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown-host";
        }
    }

    public <E> ApiError<E> createApiError(HttpStatus status, String errorCode, E message, WebRequest request) {
        ApiError<E> apiError = new ApiError<>();
        apiError.setStatus(status.value());

        ExceptionDetails<E> exceptionDetails = new ExceptionDetails<>();
        exceptionDetails.setPath(request.getDescription(false).replace("uri=", ""));
        exceptionDetails.setErrorCode(errorCode);
        exceptionDetails.setErrorTime(new Date());
        exceptionDetails.setMessage(message);
        exceptionDetails.setHostname(getHostName());

        apiError.setExceptionDetails(exceptionDetails);
        return apiError;
    }
}
