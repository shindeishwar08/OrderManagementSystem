package com.example.oms.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.oms.auth.dto.AuthResponse;
import com.example.oms.auth.dto.LoginRequest;
import com.example.oms.auth.dto.RegisterRequest;
import com.example.oms.auth.service.AuthService;
import com.example.oms.user.dto.UserMeResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    
    private final AuthService authService;

    // POST /auth/register
    @PostMapping("/register")
    public ResponseEntity<UserMeResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    
    // POST /auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    
}
