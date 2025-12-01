package com.example.oms.order.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.oms.common.exception.InvalidStateException;
import com.example.oms.common.exception.ResourceNotFoundException;
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
    
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(()-> new ResourceNotFoundException("Order not found"));

        if(!order.getCustomer().getId().equals(customer.getId())){
            throw new org.springframework.security.access.AccessDeniedException("Access Denied: You do not own this order");
        }

        if(order.getStatus()!=OrderStatus.CREATED && order.getStatus()!=OrderStatus.ASSIGNED){
            throw new InvalidStateException("Cannot cancel order in status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);

        OrderEntity savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }

    public List<OrderResponse> getPartnerOrders(UserEntity partner){
        List<OrderStatus> statuses = List.of(OrderStatus.ASSIGNED, OrderStatus.ACCEPTED, OrderStatus.PICKED);

        List<OrderEntity> entities = orderRepository.findAllByPartnerAndStatusIn(partner, statuses);

        List<OrderResponse> responses = new ArrayList<>();

        for(int i=0;i<entities.size();i++){
            responses.add(orderMapper.toResponse(entities.get(i)));
        }

        return responses;
    }

    //Accept Order
    public OrderResponse acceptOrder(Long orderId, UserEntity partner) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));

        if (order.getStatus() != OrderStatus.ASSIGNED) {
            throw new InvalidStateException("Operation failed: Order is not currently ASSIGNED");
        }

        if (!order.getPartner().getId().equals(partner.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Access Denied: This order is not assigned to you");
        }

        order.setStatus(OrderStatus.ACCEPTED);
        OrderEntity savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    //Decline Order
    public OrderResponse declineOrder(Long orderId, UserEntity partner) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));

        if (order.getStatus() != OrderStatus.ASSIGNED) {
            throw new InvalidStateException("Operation failed: Order is not currently ASSIGNED");
        }

        if (!order.getPartner().getId().equals(partner.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Access Denied: This order is not assigned to you");
        }

        order.setPartner(null);
        order.setStatus(OrderStatus.CREATED);

        OrderEntity savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus, UserEntity partner){
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(()-> new ResourceNotFoundException("Order Not Found"));

        if(order.getPartner()==null || !order.getPartner().getId().equals(partner.getId())){
            throw new org.springframework.security.access.AccessDeniedException("Access Denied: This order is not assigned to you");
        }

        OrderStatus currStatus = order.getStatus();
        boolean isValid = false;

        if(currStatus==OrderStatus.ACCEPTED && newStatus==OrderStatus.PICKED){
            isValid=true;
        }else if(currStatus==OrderStatus.PICKED && newStatus==OrderStatus.DELIVERED){
            isValid=true;
        }

        if (!isValid) throw new InvalidStateException("Invalid transition: Cannot go from " + currStatus + " to " + newStatus);
        

        order.setStatus(newStatus);
        OrderEntity savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);

    }

}
