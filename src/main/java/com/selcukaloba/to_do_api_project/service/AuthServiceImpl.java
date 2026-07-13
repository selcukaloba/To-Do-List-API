package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.dto.RegisterRequest;
import com.selcukaloba.to_do_api_project.dto.UserResponse;
import com.selcukaloba.to_do_api_project.dto.auth.AuthRequest;
import com.selcukaloba.to_do_api_project.dto.auth.AuthResponse;
import com.selcukaloba.to_do_api_project.dto.auth.RefreshTokenRequest;
import com.selcukaloba.to_do_api_project.entity.RefreshToken;
import com.selcukaloba.to_do_api_project.entity.User;
import com.selcukaloba.to_do_api_project.jwt.JwtService;
import com.selcukaloba.to_do_api_project.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
public class AuthServiceImpl implements IAuthService{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IRefreshTokenService refreshTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponse register(RegisterRequest registerRequest)
    {
        User user = new User();
        UserResponse userResponse = new UserResponse();

        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        User savedUser = userRepository.save(user);
        BeanUtils.copyProperties(savedUser, userResponse);
        return userResponse;
    }

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        try
        {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Username or password is not correct!");
        }

        Optional<User> optionalUser = userRepository.findByUsername(authRequest.getUsername());

        if(optionalUser.isEmpty()){
            throw new RuntimeException("User could not be found!");
        }

        User user = optionalUser.get();
        String accessToken = jwtService.generateToken(user.getUsername());
        String refreshToken = refreshTokenService.createRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken, user.getUsername());

    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        RefreshToken dbToken = refreshTokenService.findByRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = dbToken.getUser();
        String newAccessToken = jwtService.generateToken(user.getUsername());
        return new AuthResponse(newAccessToken, refreshTokenRequest.getRefreshToken(), user.getUsername());
    }
}
