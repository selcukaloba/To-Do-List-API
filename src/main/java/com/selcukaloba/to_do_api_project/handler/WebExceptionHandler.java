package com.selcukaloba.to_do_api_project.handler;

import com.selcukaloba.to_do_api_project.exception.BaseException;
import com.selcukaloba.to_do_api_project.exception.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

public class WebExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(BaseException.class)
    public String handleBaseException(BaseException ex, HttpServletRequest request, RedirectAttributes redirectAttributes, Locale locale) {
        ErrorMessage errorMessage = ex.getErrorMessage();
        String messageKey = errorMessage.getMessageType().getMessage();
        String localizedMessage = messageSource.getMessage(messageKey, null, messageKey, locale);

        if (errorMessage.getDetail() != null && !errorMessage.getDetail().isEmpty()) {
            localizedMessage += " : " + errorMessage.getDetail();
        }
        redirectAttributes.addFlashAttribute("errorMsg", localizedMessage);
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMsg", "An unexpected error occurred: " + ex.getMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/dashboard");
    }
}
