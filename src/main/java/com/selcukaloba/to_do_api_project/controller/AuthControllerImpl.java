package com.selcukaloba.to_do_api_project.controller;

import com.selcukaloba.to_do_api_project.dto.RegisterRequest;
import com.selcukaloba.to_do_api_project.dto.UserResponse;
import com.selcukaloba.to_do_api_project.dto.auth.AuthRequest;
import com.selcukaloba.to_do_api_project.dto.auth.AuthResponse;
import com.selcukaloba.to_do_api_project.dto.auth.RefreshTokenRequest;
import com.selcukaloba.to_do_api_project.service.IAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthControllerImpl implements IAuthController{

    @Autowired
    private IAuthService authService;

    @Override
    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest registerRequest)
    {
        return authService.register(registerRequest);
    }

    @PostMapping("/authenticate")
    @Override
    public AuthResponse authenticate(@Valid @RequestBody AuthRequest authRequest) {
        return authService.authenticate(authRequest);
    }

    @PostMapping("/refreshToken")
    @Override
    public AuthResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }
}
