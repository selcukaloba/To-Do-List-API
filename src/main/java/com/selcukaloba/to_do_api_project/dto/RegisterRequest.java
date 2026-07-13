package com.selcukaloba.to_do_api_project.dto;

import com.selcukaloba.to_do_api_project.validator.MailExtension;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotEmpty
    private String username;
    @NotEmpty
    @MailExtension
    private String email;
    @NotEmpty
    private String password;
}
