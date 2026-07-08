package com.selcukaloba.to_do_api_project.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MailExtensionValidator implements ConstraintValidator<MailExtension, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if(value == null) return true;
        return value.toString().endsWith("@mail.com");
    }
}
