package com.example.oms.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.oms.user.dto.UserMeResponse;
import com.example.oms.user.entity.UserEntity;
import com.example.oms.user.service.PartnerService;
import com.example.oms.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/partner")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PARTNER')") 
public class PartnerController {

    private final PartnerService partnerService;
    private final UserService userService;

    
    @PutMapping("/status") 
    public ResponseEntity<UserMeResponse> updateAvailability(@RequestParam boolean available, @AuthenticationPrincipal UserDetails userDetails) {

        UserEntity partner = userService.findByEmail(userDetails.getUsername());

        return ResponseEntity.ok(partnerService.updateAvailability(partner, available));
    }
}