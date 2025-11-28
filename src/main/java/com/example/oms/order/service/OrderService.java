package com.example.oms.order.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.oms.order.dto.CreateOrderRequest;
import com.example.oms.order.dto.OrderResponse;
import com.example.oms.order.entity.OrderEntity;
import com.example.oms.order.entity.OrderStatus;
import com.example.oms.order.mapper.OrderMapper;
import com.example.oms.order.repository.OrderRepository;
import com.example.oms.user.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class OrderService {
    
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;

    //New Order Creation
    public OrderResponse createOrder(CreateOrderRequest request, UserEntity customer){
        
        OrderEntity entity = orderMapper.toEntity(request, customer);

        OrderEntity savedOrder = orderRepository.save(entity);

        return orderMapper.toResponse(savedOrder);

    }


    //Logged-In customer wants to see his orders
    // (PRO TIP:- USE .stream() instead of loop but, loop is more efficient and high performance)
    public List<OrderResponse> listCustomerOrders(UserEntity customer){

        List<OrderEntity> orders = orderRepository.findByCustomer(customer);

        List<OrderResponse> response = new ArrayList<>();

        for(int i=0;i<orders.size();i++){
            response.add(orderMapper.toResponse(orders.get(i)));
        }

        return response;

    }

    //Order Cancellation
    public OrderResponse cancelOrder(Long orderId, UserEntity customer){
    
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(()-> new RuntimeException("Order not found"));

        if(!order.getCustomer().getId().equals(customer.getId())){
            throw new RuntimeException("Access Denied: You do not own this order");
        }

        if(order.getStatus()!=OrderStatus.CREATED && order.getStatus()!=OrderStatus.ASSIGNED){
            throw new RuntimeException("Cannot cancel order in status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);

        OrderEntity savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }

}
