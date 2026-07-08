package com.selcukaloba.to_do_api_project.validator;

import com.selcukaloba.to_do_api_project.dto.TodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.TodoUpdateRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class TaskDateValidator implements ConstraintValidator<TaskDateMatch, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime reminderDate = null;
        LocalDateTime dueDate = null;

        if(value instanceof TodoCreateRequest)
        {
            TodoCreateRequest createRequest = (TodoCreateRequest) value;
            reminderDate = createRequest.getReminderDate();
            dueDate = createRequest.getDueDate();
        }

        else if(value instanceof TodoUpdateRequest)
        {
            TodoUpdateRequest updateRequest = (TodoUpdateRequest) value;
            reminderDate = updateRequest.getReminderDate();
            dueDate = updateRequest.getDueDate();
        }

        if(reminderDate == null || dueDate == null) {return true;}

        if(reminderDate.isAfter(dueDate)){return false;}

        return true;
    }

}
