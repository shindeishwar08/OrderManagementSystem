package com.example.oms.order.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.oms.common.exception.ResourceNotFoundException;
import com.example.oms.order.entity.OrderEntity;
import com.example.oms.order.entity.OrderStatus;
import com.example.oms.order.repository.OrderRepository;
import com.example.oms.user.entity.Role;
import com.example.oms.user.entity.UserEntity;
import com.example.oms.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    //'synchronized' here for simple thread-safety
    public synchronized void assignOrder(OrderEntity order) {

        List<UserEntity> partners = userRepository.findAllByRoleAndAvailable(Role.PARTNER, true);
        if (partners.isEmpty()) {
            throw new ResourceNotFoundException("No partners available");
        }

        UserEntity bestPartner = null;
        long minLoad = Long.MAX_VALUE;

        List<OrderStatus> activeStatuses = List.of(OrderStatus.ASSIGNED, OrderStatus.ACCEPTED, OrderStatus.PICKED);

        for (UserEntity p : partners) {
            Long load = orderRepository.countByPartnerIdAndStatusIn(p.getId(), activeStatuses);

            if (load < minLoad) {
                minLoad = load;
                bestPartner = p;
            }
        }

        order.setPartner(bestPartner);
        order.setStatus(OrderStatus.ASSIGNED);

        orderRepository.save(order);
    }

    public OrderEntity manualAssign(OrderEntity order, UserEntity partner){
        order.setPartner(partner);
        order.setStatus(OrderStatus.ASSIGNED);

        return orderRepository.save(order);
    }
}