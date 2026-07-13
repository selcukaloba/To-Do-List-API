package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.entity.RefreshToken;
import com.selcukaloba.to_do_api_project.entity.User;
import com.selcukaloba.to_do_api_project.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements IRefreshTokenService{

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Override
    public String createRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        Date expireDate = new Date(System.currentTimeMillis()+ (1000L * 60 * 24 * 60 * 7));

        RefreshToken refreshToken= new RefreshToken();
        refreshToken.setRefreshToken(token);
        refreshToken.setExpiryDate(expireDate);
        refreshToken.setUser(user);
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    @Override
    public RefreshToken findByRefreshToken(String token) {
        Optional<RefreshToken> optional = refreshTokenRepository.findByRefreshToken(token);
        if(optional.isEmpty())
        {
            throw new RuntimeException("Invalid refresh token request!");
        }
        RefreshToken refreshToken = optional.get();
        if(refreshToken.getExpiryDate().before(new Date()))
        {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired!");
        }
        return refreshToken;
    }
}
