package com.selcukaloba.to_do_api_project.exception;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {
    private String detail;
    private MessageType messageType;

    public String generateErrorMessage()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(messageType.getMessage());
        if(detail!=null)
        {
            builder.append(" : " +detail);
        }
        return builder.toString();
    }
}
