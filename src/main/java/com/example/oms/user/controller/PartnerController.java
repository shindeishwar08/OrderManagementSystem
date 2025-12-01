package com.example.oms.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.oms.order.dto.OrderResponse;
import com.example.oms.order.dto.OrderStatusUpdateRequest;
import com.example.oms.order.service.OrderService;
import com.example.oms.user.dto.UserMeResponse;
import com.example.oms.user.entity.UserEntity;
import com.example.oms.user.service.PartnerService;
import com.example.oms.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/partner")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PARTNER')") 
public class PartnerController {

    private final PartnerService partnerService;
    private final UserService userService;
    private final OrderService orderService;

    
    @PutMapping("/status") 
    public ResponseEntity<UserMeResponse> updateAvailability(@RequestParam boolean available, @AuthenticationPrincipal UserDetails userDetails) {

        UserEntity partner = userService.findByEmail(userDetails.getUsername());

        return ResponseEntity.ok(partnerService.updateAvailability(partner, available));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails){
        UserEntity partner = userService.findByEmail(userDetails.getUsername());

        return ResponseEntity.ok(orderService.getPartnerOrders(partner));
    }

    @PutMapping("/orders/accept/{orderId}")
    public ResponseEntity<OrderResponse> acceptOrder(@PathVariable Long orderId, @AuthenticationPrincipal UserDetails userDetails){
        UserEntity partner = userService.findByEmail(userDetails.getUsername());

        return ResponseEntity.ok(orderService.acceptOrder(orderId, partner));
    }

    @PutMapping("/orders/decline/{orderId}")
    public ResponseEntity<OrderResponse> declineOrder(@PathVariable Long orderId, @AuthenticationPrincipal UserDetails userDetails){
        UserEntity partner = userService.findByEmail(userDetails.getUsername());

        return ResponseEntity.ok(orderService.declineOrder(orderId, partner));
    }

    @PutMapping("/orders/update/{orderId}")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long orderId, @AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid OrderStatusUpdateRequest request){
        
        UserEntity partner = userService.findByEmail(userDetails.getUsername());

        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request.getOrderStatus(), partner));
    }
}