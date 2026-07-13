package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.entity.RefreshToken;
import com.selcukaloba.to_do_api_project.entity.User;

public interface IRefreshTokenService {
    String createRefreshToken(User user);
    RefreshToken findByRefreshToken(String token);
}
