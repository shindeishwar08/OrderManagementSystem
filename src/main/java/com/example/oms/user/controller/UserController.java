package com.example.oms.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.oms.user.dto.UserMeResponse;
import com.example.oms.user.entity.UserEntity;
import com.example.oms.user.mapper.UserMapper;
import com.example.oms.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> me(@AuthenticationPrincipal UserDetails userDetails){

        UserEntity user = userService.findByEmail(userDetails.getUsername());

        return ResponseEntity.ok(userMapper.toUserMeResponse(user));

    }
}
