package com.example.oms.user.service;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.oms.auth.dto.RegisterRequest;
import com.example.oms.user.dto.UserMeResponse;
import com.example.oms.user.entity.UserEntity;
import com.example.oms.user.mapper.UserMapper;
import com.example.oms.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


        public UserMeResponse register(RegisterRequest request){

            if(userRepository.findByEmail(request.getEmail()).isPresent()){
                throw new RuntimeException("Email already exists");
            }

            UserEntity user = UserEntity.builder().name(request.getName()).email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).role(request.getRole()).build();
            UserEntity savedUser = userRepository.save(user);

            return userMapper.toUserMeResponse(savedUser);

        }

        public UserEntity findByEmail(String email){
            return userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found by email " + email));
        }

        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
            UserEntity user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found with email"+email));
        
            return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),new ArrayList<>());
        }

}
