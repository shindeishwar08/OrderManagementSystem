package com.example.oms.auth.service;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.example.oms.auth.dto.AuthResponse;
import com.example.oms.auth.dto.LoginRequest;
import com.example.oms.auth.dto.RegisterRequest;
import com.example.oms.auth.util.JwtUtil;
import com.example.oms.user.dto.UserMeResponse;
import com.example.oms.user.entity.UserEntity;
import com.example.oms.user.service.UserService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService{
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager; //From SecurityConfig.java


    public UserMeResponse register(RegisterRequest request){
        return userService.register(request);
    }



    public AuthResponse login(LoginRequest request){
        //authenticate the user, ps:- authentication manager does the job
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));

        //if authenticated get the user from db
        UserEntity user = userService.findByEmail(request.getEmail());

        //obviously now generate a token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        //return authresponse to user
        return AuthResponse.builder().token(token).userId(user.getId()).role(user.getRole()).build();
    }
}