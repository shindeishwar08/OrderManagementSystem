package com.example.oms.order.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.oms.order.dto.CreateOrderRequest;
import com.example.oms.order.dto.OrderResponse;
import com.example.oms.order.entity.OrderEntity;
import com.example.oms.order.entity.OrderStatus;
import com.example.oms.user.entity.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    
    private final ObjectMapper objectMapper;

    // Request -> Entity
    public OrderEntity toEntity(CreateOrderRequest request, UserEntity customer){
        if(request == null){
            return null;
        }

        try {
            return OrderEntity.builder()
                    .customer(customer)
                    .status(OrderStatus.CREATED)
                    .pickupAddress(request.getPickupAddress())
                    .deliveryAddress(request.getDeliveryAddress())
                    // Convert List -> JSON String
                    .itemsJson(objectMapper.writeValueAsString(request.getItems()))
                    .totalAmount(request.getTotalAmount())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting to JSON", e);
        }
    }

    // Entity -> Response
    public OrderResponse toResponse(OrderEntity entity){
        if(entity == null){
            return null;
        }

        try {
            // Convert JSON String -> List<String> (Added type safety here)
            List<String> items = objectMapper.readValue(
                entity.getItemsJson(), 
                new TypeReference<List<String>>() {}
            );

            return OrderResponse.builder()
                    .id(entity.getId())
                    .status(entity.getStatus())
                    .customerId(entity.getCustomer().getId())
                    .partnerId(entity.getPartner() != null ? entity.getPartner().getId() : null)
                    .pickupAddress(entity.getPickupAddress())
                    .deliveryAddress(entity.getDeliveryAddress())
                    .items(items)
                    .totalAmount(entity.getTotalAmount())
                    .createdAt(entity.getCreatedAt())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error reading items from JSON", e);
        }
    }
}