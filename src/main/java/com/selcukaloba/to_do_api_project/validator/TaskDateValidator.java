package com.selcukaloba.to_do_api_project.validator;

import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoCreateRequest;
import com.selcukaloba.to_do_api_project.dto.todo.ApiTodoUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class TaskDateValidator implements ConstraintValidator<TaskDateMatch, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime reminderDate = null;
        LocalDateTime dueDate = null;

        if(value instanceof ApiTodoCreateRequest)
        {
            ApiTodoCreateRequest createRequest = (ApiTodoCreateRequest) value;
            reminderDate = createRequest.getReminderDate();
            dueDate = createRequest.getDueDate();
        }

        else if(value instanceof ApiTodoUpdateRequest)
        {
            ApiTodoUpdateRequest updateRequest = (ApiTodoUpdateRequest) value;
            reminderDate = updateRequest.getReminderDate();
            dueDate = updateRequest.getDueDate();
        }

        if(reminderDate == null || dueDate == null) {return true;}

        if(reminderDate.isAfter(dueDate)){return false;}

        return true;
    }

}
