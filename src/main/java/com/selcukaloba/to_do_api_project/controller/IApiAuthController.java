package com.selcukaloba.to_do_api_project.controller;

import com.selcukaloba.to_do_api_project.dto.ApiRegisterRequest;
import com.selcukaloba.to_do_api_project.dto.ApiUserResponse;
import com.selcukaloba.to_do_api_project.dto.auth.ApiAuthRequest;
import com.selcukaloba.to_do_api_project.dto.auth.ApiAuthResponse;
import com.selcukaloba.to_do_api_project.dto.auth.ApiRefreshTokenRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

public interface IApiAuthController {
    ApiUserResponse register(@Valid @RequestBody ApiRegisterRequest registerRequest);
    ApiAuthResponse authenticate(ApiAuthRequest authRequest);
    ApiAuthResponse refreshToken(@Valid @RequestBody ApiRefreshTokenRequest refreshTokenRequest);
}
