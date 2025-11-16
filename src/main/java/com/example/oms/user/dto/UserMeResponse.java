package com.example.oms.user.dto;

import com.example.oms.user.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserMeResponse {
    
    private long id;

    private String name;

    private String email;

    private Role role;

    private boolean available;

}
