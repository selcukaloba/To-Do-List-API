package com.selcukaloba.to_do_api_project.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiAuthResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
}
