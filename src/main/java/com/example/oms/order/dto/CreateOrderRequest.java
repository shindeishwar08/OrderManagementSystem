package com.example.oms.order.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {
    
    @NotBlank(message="Pickup address is required")
    private String pickupAddress;

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;
    
    @NotNull(message = "Items list cannot be null")
    private List<String> items;

    @NotNull(message="Total amount is required")
    private Double totalAmount;
}
