package com.example.oms.location.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PartnerLocationRequest {
    @NotNull
    @Min(-90) @Max(90)
    Double latitude;
    
    @NotNull
    @Min(-180) @Max(180)
    Double longitude;
}
