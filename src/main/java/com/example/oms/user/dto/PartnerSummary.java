package com.example.oms.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerSummary {
    private Long id;
    private String name;
    private String email;
    private boolean available;
    private Long currentLoad;
}
