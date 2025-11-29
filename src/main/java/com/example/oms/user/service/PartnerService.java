package com.example.oms.user.service;

import org.springframework.stereotype.Service;

import com.example.oms.user.dto.UserMeResponse;
import com.example.oms.user.entity.Role;
import com.example.oms.user.entity.UserEntity;
import com.example.oms.user.mapper.UserMapper;
import com.example.oms.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PartnerService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    //Partner can update their availability status
    public UserMeResponse updateAvailability(UserEntity partner, boolean available){

        if(partner.getRole()!= Role.PARTNER){
            throw new org.springframework.security.access.AccessDeniedException("User not partner");
        }

        partner.setAvailable(available);

        UserEntity savedPartner = userRepository.save(partner);

        return userMapper.toUserMeResponse(savedPartner);
    }

}
