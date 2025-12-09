package com.example.oms.order.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.oms.common.exception.ResourceNotFoundException;
import com.example.oms.location.service.LocationService;
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
    private final LocationService locationService;

    //'synchronized' here for simple thread-safety
    public synchronized void assignOrder(OrderEntity order) {

        double warehouseLat = 18.5204;
        double warehouseLon = 73.8567;

        List<String> nearByIds = locationService.findNearestPartners(warehouseLat, warehouseLon, 3.0);

        List<UserEntity> partners = new ArrayList<>();

        for(String nearById : nearByIds){
            var optionalPartner = userRepository.findById(Long.valueOf(nearById));

            if(optionalPartner.isPresent()){
                UserEntity partner = optionalPartner.get();
                if(partner.isAvailable()) partners.add(partner);
            }
        }

        if(partners.isEmpty()){
            partners = userRepository.findAllByRoleAndAvailable(Role.PARTNER, true);
        }

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