package com.selcukaloba.to_do_api_project.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})//tek alan etkilenecek, field-level
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MailExtensionValidator.class)
@Documented
public @interface MailExtension {
    String message() default "Email format only must be in @mail.com format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
