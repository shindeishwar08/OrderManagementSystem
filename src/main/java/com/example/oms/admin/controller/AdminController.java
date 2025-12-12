package com.example.oms.admin.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.oms.admin.dto.AnalyticsResponse;
import com.example.oms.location.dto.TrackingResponse;
import com.example.oms.order.dto.OrderResponse;
import com.example.oms.order.entity.OrderStatus;
import com.example.oms.order.service.OrderService;
import com.example.oms.user.dto.PartnerSummary;
import com.example.oms.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final OrderService orderService;
    private final UserService userService;

    // 1. List All Orders
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long partnerId
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(orderService.getAllOrders(pageable, status, partnerId));
    }

    // 2. Manual Assign (Override)
    @PutMapping("/orders/assign/{orderId}")
    public ResponseEntity<OrderResponse> manualAssign(
            @PathVariable Long orderId,
            @RequestParam Long partnerId
    ) {
        return ResponseEntity.ok(orderService.manualAssign(orderId, partnerId));
    }

    // 3. Partner Roster (Load & Status)
    @GetMapping("/partners")
    public ResponseEntity<List<PartnerSummary>> getPartners(@RequestParam(required=false) Boolean available){
        return ResponseEntity.ok(userService.getAllPartners(available));
    }

    // 4. Admin Analytics
    @GetMapping("/stats")
    public ResponseEntity<AnalyticsResponse> getAnalytics() {
        return ResponseEntity.ok(orderService.getAnalytics());
    }

    //5. Track Partner
    @GetMapping("/partner/{partnerId}")
    public ResponseEntity<TrackingResponse> getPartnerLocation(@PathVariable Long partnerId){
        
        TrackingResponse response = userService.trackPartner(partnerId);

        return ResponseEntity.ok(response);
    }
}