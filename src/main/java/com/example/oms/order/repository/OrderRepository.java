package com.example.oms.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.oms.order.entity.OrderEntity;
import com.example.oms.user.entity.UserEntity;


public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    
    List<OrderEntity> findByCustomer(UserEntity customer);
}
