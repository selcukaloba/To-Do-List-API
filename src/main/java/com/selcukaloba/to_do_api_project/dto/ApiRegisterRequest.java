package com.selcukaloba.to_do_api_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiRegisterRequest {
    @NotBlank(message ="{user.username.not_blank}")
    private String username;
    @NotBlank(message = "{user.email.not_blank}")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
            message = "{user.email.invalid}"
    )
    private String email;
    @NotBlank(message = "{user.password.not_blank}")
    private String password;
}
