package com.selcukaloba.to_do_api_project.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TaskDateValidator.class)
@Documented
public @interface TaskDateMatch {
    String message() default "Reminder date should be before than due date.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
