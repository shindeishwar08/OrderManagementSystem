package com.example.oms.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    private Long totalOrders;
    private Long activeOrders;
    private Long deliveredOrders;
    private Double totalRevenue;
}
