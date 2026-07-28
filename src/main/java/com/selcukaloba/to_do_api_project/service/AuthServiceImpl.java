package com.selcukaloba.to_do_api_project.service;

import com.selcukaloba.to_do_api_project.dto.ApiRegisterRequest;
import com.selcukaloba.to_do_api_project.dto.ApiUserResponse;
import com.selcukaloba.to_do_api_project.dto.auth.ApiAuthRequest;
import com.selcukaloba.to_do_api_project.dto.auth.ApiAuthResponse;
import com.selcukaloba.to_do_api_project.dto.auth.ApiRefreshTokenRequest;
import com.selcukaloba.to_do_api_project.entity.RefreshToken;
import com.selcukaloba.to_do_api_project.entity.User;
import com.selcukaloba.to_do_api_project.exception.BaseException;
import com.selcukaloba.to_do_api_project.exception.ErrorMessage;
import com.selcukaloba.to_do_api_project.exception.MessageType;
import com.selcukaloba.to_do_api_project.jwt.JwtService;
import com.selcukaloba.to_do_api_project.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public ApiUserResponse register(ApiRegisterRequest registerRequest)
    {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BaseException(new ErrorMessage("Username: " + registerRequest.getUsername(), MessageType.USER_ALREADY_EXISTS));
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BaseException(new ErrorMessage("Email: " + registerRequest.getEmail(), MessageType.USER_ALREADY_EXISTS));
        }
        User user = new User();
        ApiUserResponse userResponse = new ApiUserResponse();

        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        User savedUser = userRepository.save(user);
        BeanUtils.copyProperties(savedUser, userResponse);
        return userResponse;
    }

    @Override
    public ApiAuthResponse authenticate(ApiAuthRequest authRequest) {
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
        return new ApiAuthResponse(accessToken, refreshToken, user.getUsername());

    }

    @Override
    public ApiAuthResponse refreshToken(ApiRefreshTokenRequest refreshTokenRequest) {
        RefreshToken dbToken = refreshTokenService.findByRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = dbToken.getUser();
        String newAccessToken = jwtService.generateToken(user.getUsername());
        return new ApiAuthResponse(newAccessToken, refreshTokenRequest.getRefreshToken(), user.getUsername());
    }
}
