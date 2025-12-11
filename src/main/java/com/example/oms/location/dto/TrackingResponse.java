package com.example.oms.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TrackingResponse {
    private String partnerId;
    private Double lat;
    private Double lon;
    private String status; 
}
