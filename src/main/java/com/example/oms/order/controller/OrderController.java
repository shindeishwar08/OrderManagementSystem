package com.example.oms.order.controller;

import java.util.List;

import org.springframework.data.geo.Point;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.oms.common.exception.InvalidStateException;
import com.example.oms.location.dto.TrackingResponse;
import com.example.oms.location.service.LocationService;
import com.example.oms.order.dto.CreateOrderRequest;
import com.example.oms.order.dto.OrderResponse;
import com.example.oms.order.entity.OrderEntity;
import com.example.oms.order.entity.OrderStatus;
import com.example.oms.order.service.OrderService;
import com.example.oms.user.entity.UserEntity;
import com.example.oms.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@PreAuthorize("hasRole('CUSTOMER')")  //RBAC for Customer
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final LocationService locationService;

    // 1. Create Order
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request, @AuthenticationPrincipal UserDetails userDetails){
                
        UserEntity user = userService.findByEmail(userDetails.getUsername());
        
        return ResponseEntity.ok(orderService.createOrder(request, user));
    }
    // 2. List My Orders
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> myOrders(@AuthenticationPrincipal UserDetails userDetails){

        UserEntity user = userService.findByEmail(userDetails.getUsername());

        return ResponseEntity.ok(orderService.listCustomerOrders(user));
    }

    // 3. Cancel Order
    @PutMapping("/cancel/{id}")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails){

        UserEntity user = userService.findByEmail(userDetails.getUsername());

        return ResponseEntity.ok(orderService.cancelOrder(id, user));
    }

    @GetMapping("/{orderId}/track")
    public ResponseEntity<TrackingResponse> getOrderLocation(@PathVariable Long orderId, @AuthenticationPrincipal UserDetails userDetails){

        UserEntity customer = userService.findByEmail(userDetails.getUsername());

        TrackingResponse response = orderService.trackOrder(orderId, customer);

        return ResponseEntity.ok(response);
    }
}