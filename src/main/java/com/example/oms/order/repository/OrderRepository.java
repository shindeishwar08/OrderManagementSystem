package com.example.oms.order.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.oms.order.entity.OrderEntity;
import com.example.oms.order.entity.OrderStatus;
import com.example.oms.user.entity.UserEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    
    // Customer: My Orders
    List<OrderEntity> findByCustomer(UserEntity customer);
    
    // Partner: Load Balancing (Count)
    Long countByPartnerIdAndStatusIn(Long partnerId, List<OrderStatus> statuses);

    // Partner: Dashboard (List)
    List<OrderEntity> findAllByPartnerAndStatusIn(UserEntity partner, List<OrderStatus> statuses);

    // ADMIN: Search with Filters + Pagination
    @Query("SELECT o FROM OrderEntity o WHERE " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:partnerId IS NULL OR o.partner.id = :partnerId) AND " +
           "(cast(:startDate as timestamp) IS NULL OR o.createdAt >= :startDate) AND " +
           "(cast(:endDate as timestamp) IS NULL OR o.createdAt <= :endDate)")
    Page<OrderEntity> findAllByFilters(
            @Param("status") OrderStatus status,
            @Param("partnerId") Long partnerId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable
    );


    // ADMIN ANALYTICS

    Long countByStatusIn(List<OrderStatus> statuses);

    Long countByStatus(OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM OrderEntity o")
    Double sumTotalAmount();


}