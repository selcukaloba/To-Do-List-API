package com.selcukaloba.to_do_api_project.controller;

import com.selcukaloba.to_do_api_project.dto.ApiRegisterRequest;
import com.selcukaloba.to_do_api_project.dto.ApiUserResponse;
import com.selcukaloba.to_do_api_project.dto.auth.ApiAuthRequest;
import com.selcukaloba.to_do_api_project.dto.auth.ApiAuthResponse;
import com.selcukaloba.to_do_api_project.dto.auth.ApiRefreshTokenRequest;
import com.selcukaloba.to_do_api_project.service.IAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class ApiAuthControllerImpl implements IApiAuthController {

    @Autowired
    private IAuthService authService;

    @Override
    @PostMapping("/register")
    public ApiUserResponse register(@Valid @RequestBody ApiRegisterRequest registerRequest)
    {
        return authService.register(registerRequest);
    }

    @PostMapping("/authenticate")
    @Override
    public ApiAuthResponse authenticate(@Valid @RequestBody ApiAuthRequest authRequest) {
        return authService.authenticate(authRequest);
    }

    @PostMapping("/refreshToken")
    @Override
    public ApiAuthResponse refreshToken(@Valid @RequestBody ApiRefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }
}
