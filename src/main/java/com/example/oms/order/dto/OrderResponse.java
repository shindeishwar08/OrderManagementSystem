package com.example.oms.order.dto;

import java.time.Instant;
import java.util.List;

import com.example.oms.order.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    
    private Long id;
    private OrderStatus status;
    private Long customerId;
    private Long partnerId;
    private String pickupAddress;
    private String deliveryAddress;
    private List<String> items;
    private Double totalAmount;
    private Instant createdAt;

}
