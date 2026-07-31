package com.selcukaloba.to_do_api_project.handler;

import com.selcukaloba.to_do_api_project.controller.web.WebController;
import com.selcukaloba.to_do_api_project.exception.BaseException;
import com.selcukaloba.to_do_api_project.exception.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Locale;

@ControllerAdvice(assignableTypes = {com.selcukaloba.to_do_api_project.controller.web.WebController.class})
public class WebExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(BaseException.class)
    public RedirectView handleBaseException(BaseException ex, HttpServletRequest request, Locale locale) {
        ErrorMessage errorMessage = ex.getErrorMessage();
        String messageKey = errorMessage.getMessageType().getMessage();
        String localizedMessage = messageSource.getMessage(messageKey, null, messageKey, locale);
        if (errorMessage.getDetail() != null && !errorMessage.getDetail().isEmpty()) {
            localizedMessage += " : " + errorMessage.getDetail();
        }
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        if (flashMap != null) {
            flashMap.put("errorMsg", localizedMessage);
        }
        String referer = request.getHeader("Referer");
        String redirectUrl = (referer != null) ? referer : "/dashboard";
        RedirectView redirectView = new RedirectView(redirectUrl);
        return redirectView;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RedirectView handleValidationException(MethodArgumentNotValidException ex,
                                                  HttpServletRequest request, Locale locale) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation failed!");

        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        if (flashMap != null) {
            flashMap.put("errorMsg", errorMessage);
        }
        String referer = request.getHeader("Referer");
        String redirectUrl = (referer != null) ? referer : "/dashboard";
        RedirectView redirectView = new RedirectView(redirectUrl);
        return redirectView;
    }

    @ExceptionHandler(Exception.class)
    public RedirectView handleGeneralException(Exception ex, HttpServletRequest request, Locale locale)
    {
        String messageKey = "general.exception";
        String localizedMessage = messageSource.getMessage(messageKey, null, "An unexpected error occurred!", locale);

        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        if (flashMap != null) {
            flashMap.put("errorMsg", localizedMessage);
        }
        String referer = request.getHeader("Referer");
        String redirectUrl = (referer != null) ? referer : "/dashboard";
        RedirectView redirectView = new RedirectView(redirectUrl);
        return redirectView;
    }
}