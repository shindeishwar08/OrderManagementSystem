package com.example.oms.auth.dto;

import com.example.oms.user.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message="Nmae cannot be blank")
    private String name;

    @NotBlank(message="Email is required")
    @Email(message="Invalid email Format")
    private String email;

    @NotBlank(message="Enter a new Password")
    @Size(min=6,message="Paswword need to be of minimum 6 characters.")
    private String password;
    private Role role; //Needs validation
}
