package com.example.oms.user.mapper;

import org.springframework.stereotype.Component;

import com.example.oms.user.dto.UserMeResponse;
import com.example.oms.user.entity.UserEntity;

@Component
public class UserMapper {
    
    
    public UserMeResponse toUserMeResponse(UserEntity user){

        if(user==null){
            return null;
        }

        return UserMeResponse.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).role(user.getRole()).available(user.isAvailable()).build();
    }
}


