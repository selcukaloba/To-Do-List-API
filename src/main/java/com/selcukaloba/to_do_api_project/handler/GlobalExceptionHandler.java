package com.selcukaloba.to_do_api_project.handler;

import com.selcukaloba.to_do_api_project.exception.BaseException;
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

    @ExceptionHandler(value = {BaseException.class})
    public ResponseEntity<ApiError<?>> handleBaseException(BaseException ex, WebRequest request)
    {
        return ResponseEntity.badRequest().body(createApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ApiError<Map<String, List<String>>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request)
    {
        Map<String, List<String>> map = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors())
        {
            String fieldName = fieldError.getField();
            if (map.containsKey(fieldName)) {
                map.put(fieldName, addValue(map.get(fieldName), fieldError.getDefaultMessage()));
            } else {
                map.put(fieldName, addValue(new ArrayList<>(), fieldError.getDefaultMessage()));
            }
        }

        for (org.springframework.validation.ObjectError objectError : ex.getBindingResult().getGlobalErrors())
        {
            String objectName = objectError.getObjectName();
            if (map.containsKey(objectName))
            {
                map.put(objectName, addValue(map.get(objectName), objectError.getDefaultMessage()));
            }
            else
            {
                map.put(objectName, addValue(new ArrayList<>(), objectError.getDefaultMessage()));
            }
        }
        return ResponseEntity.badRequest().body(createApiError(HttpStatus.BAD_REQUEST, map, request));
    }

    private List<String> addValue(List<String> list, String newValue)
    {
        list.add(newValue);
        return list;
    }

    private String getHostName()
    {
        try {
            return Inet4Address.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }

    public <E> ApiError<E> createApiError(HttpStatus status, E message, WebRequest request)
    {
        ApiError<E> apiError = new ApiError<E>();
        apiError.setStatus(status.value());
        ExceptionDetails<E> exceptionDetails = new ExceptionDetails<>();
        exceptionDetails.setPath(request.getDescription(false).substring(4));
        exceptionDetails.setErrorTime(new Date());
        exceptionDetails.setMessage(message);
        exceptionDetails.setHostname(getHostName());

        apiError.setExceptionDetails(exceptionDetails);
        return apiError;
    }
}
