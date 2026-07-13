package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.dto.RegisterRequest;
import com.selcukaloba.to_do_api_project.dto.UserResponse;
import com.selcukaloba.to_do_api_project.dto.auth.AuthRequest;
import com.selcukaloba.to_do_api_project.dto.auth.AuthResponse;
import com.selcukaloba.to_do_api_project.dto.auth.RefreshTokenRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

public interface IAuthService {
    UserResponse register(RegisterRequest registerRequest);

    AuthResponse authenticate(AuthRequest authRequest);
    AuthResponse refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest);
}
