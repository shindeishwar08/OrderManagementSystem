package com.example.oms.order.entity;

import com.example.oms.common.BaseAudit;
import com.example.oms.user.entity.UserEntity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name="orders")
public class OrderEntity extends BaseAudit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable=false)
    private UserEntity customer;

    @ManyToOne// A
    @JoinColumn(name="partner_id")// It is nullable as initially when order is created partner is not assigned, system assigns it after order is created.
    private UserEntity partner;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private OrderStatus status;

    @Column(nullable=false)
    private String pickupAddress;

    @Column(nullable=false)
    private String deliveryAddress;

    @Column(nullable=false, columnDefinition = "TEXT")
    private String itemsJson;

    private double totalAmount;
}
