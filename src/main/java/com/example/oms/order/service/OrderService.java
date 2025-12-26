package com.example.oms.order.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.oms.admin.dto.AnalyticsResponse;
import com.example.oms.common.exception.InvalidStateException;
import com.example.oms.common.exception.ResourceNotFoundException;
import com.example.oms.location.dto.TrackingResponse;
import com.example.oms.location.service.LocationService;
import com.example.oms.order.dto.CreateOrderRequest;
import com.example.oms.order.dto.OrderResponse;
import com.example.oms.order.entity.OrderEntity;
import com.example.oms.order.entity.OrderStatus;
import com.example.oms.order.mapper.OrderMapper;
import com.example.oms.order.repository.OrderRepository;
import com.example.oms.user.entity.UserEntity;
import com.example.oms.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class OrderService {
    
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final AssignmentService assignmentService;
    private final UserRepository userRepository;
    private final LocationService locationService;

    //New Order Creation
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, UserEntity customer){
        
        OrderEntity entity = orderMapper.toEntity(request, customer);

        OrderEntity savedOrder = orderRepository.save(entity);

        assignmentService.assignOrder(savedOrder);

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
    @Transactional
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
    @Transactional
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
    @Transactional
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

        assignmentService.assignOrder(savedOrder);
        
        return orderMapper.toResponse(savedOrder);
    }

    //Final Order Lifecycle transitions
    @Transactional
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

    // Get All Orders (Filtered & Paged)
    public Page<OrderResponse> getAllOrders(Pageable pageable, OrderStatus status, Long partnerId){
        Page<OrderEntity> response = orderRepository.findAllByFilters(status, partnerId, null, null, pageable);

        return response.map(orderMapper::toResponse);
    }


    //Assign order manually
    public OrderResponse manualAssign(Long orderId, Long partnerId){
        OrderEntity order = orderRepository.findById(orderId).orElseThrow(()-> new ResourceNotFoundException("Order Not Found"));

        UserEntity partner = userRepository.findById(partnerId).orElseThrow(()-> new ResourceNotFoundException("Partner Not Found"));

        OrderEntity updatedOrder = assignmentService.manualAssign(order, partner);

        return orderMapper.toResponse(updatedOrder);
    }

    public AnalyticsResponse getAnalytics(){

        List<OrderStatus> activeStatuses = List.of(OrderStatus.ASSIGNED,OrderStatus.ACCEPTED,OrderStatus.PICKED);
        
        Long totalOrders = orderRepository.count();

        Long actives = orderRepository.countByStatusIn(activeStatuses);

        Long delivered = orderRepository.countByStatus(OrderStatus.DELIVERED);
        
        Double totalRevenue= orderRepository.sumTotalAmount();
        if(orderRepository.sumTotalAmount()==null){
            totalRevenue=0.0;
        }

        return AnalyticsResponse.builder().totalOrders(totalOrders).activeOrders(actives).deliveredOrders(delivered).totalRevenue(totalRevenue).build();

    }

    public OrderResponse getOrderById(Long id){
        OrderEntity order = orderRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Order Not Found"));

        return orderMapper.toResponse(order);

    }

    public TrackingResponse trackOrder(Long orderId, UserEntity customer){

        OrderEntity order = orderRepository.findById(orderId).orElseThrow(()-> new ResourceNotFoundException("Order Not Found"));
        if(order.getCustomer().getId()!=customer.getId()) throw new InvalidStateException("Order does not belong to you");

        List<OrderStatus> trackableStatuses = List.of(OrderStatus.ACCEPTED, OrderStatus.PICKED);

        if (!trackableStatuses.contains(order.getStatus())) {
            if (order.getStatus() == OrderStatus.ASSIGNED || order.getStatus() == OrderStatus.CREATED) {
                throw new InvalidStateException("Waiting for partner to accept the order...");
            } else {
                throw new InvalidStateException("Tracking is not active for order status: " + order.getStatus());
            }
        }

        if(order.getPartner()==null) throw new InvalidStateException("Order is yet to be Assigned");

        Point location = locationService.getPartnerLocation(order.getPartner().getId());
        if (location == null) {
            throw new InvalidStateException("Partner location unavailable (Waiting for GPS)");
        }

        TrackingResponse response = TrackingResponse.builder().partnerId(String.valueOf(order.getPartner().getId())).lat(location.getY()).lon(location.getX()).status(String.valueOf(order.getStatus())).build();

        return response;
    }

}
