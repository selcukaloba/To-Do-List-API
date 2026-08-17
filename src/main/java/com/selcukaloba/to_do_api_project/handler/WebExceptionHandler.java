package com.selcukaloba.to_do_api_project.handler;

import com.selcukaloba.to_do_api_project.controller.web.*;
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

@ControllerAdvice(assignableTypes = {
        AuthController.class,
        DashboardController.class,
        FriendsController.class,
        TeamController.class,
        CommentController.class
})
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
        return new RedirectView(redirectUrl);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RedirectView handleValidationException(MethodArgumentNotValidException ex,HttpServletRequest request, Locale locale) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String localizedMessage;
        if (fieldError != null)
        {
            String code = fieldError.getDefaultMessage().replaceAll("[{}]", "");
            localizedMessage = messageSource.getMessage(code, null, code, locale);
        } else
        {
            localizedMessage = messageSource.getMessage("validation.error", null, locale);
        }

        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        if (flashMap != null) {
            flashMap.put("errorMsg", localizedMessage);
        }

        String referer = request.getHeader("Referer");
        String redirectUrl = (referer != null) ? referer : "/dashboard";
        return new RedirectView(redirectUrl);
    }

    @ExceptionHandler(Exception.class)
    public RedirectView handleGeneralException(Exception ex, HttpServletRequest request, Locale locale) {
        ex.printStackTrace();  // BU SATIRI EKLE - konsola hatayı yazdırır
        System.out.println("=== GENERAL HANDLER: " + ex.getClass().getName() + " ===");
        System.out.println("Message: " + ex.getMessage());
        String localizedMessage = messageSource.getMessage("general.exception", null, "An unexpected error occurred!", locale);

        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        if (flashMap != null) {
            flashMap.put("errorMsg", localizedMessage);
        }

        String referer = request.getHeader("Referer");
        String redirectUrl = (referer != null) ? referer : "/dashboard";
        return new RedirectView(redirectUrl);
    }
}