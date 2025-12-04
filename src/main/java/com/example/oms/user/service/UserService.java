package com.example.oms.user.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.oms.auth.dto.RegisterRequest;
import com.example.oms.common.exception.EmailAlreadyExistsException;
import com.example.oms.order.entity.OrderStatus;
import com.example.oms.order.repository.OrderRepository;
import com.example.oms.user.dto.PartnerSummary;
import com.example.oms.user.dto.UserMeResponse;
import com.example.oms.user.entity.Role;
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
    private final OrderRepository orderRepository;


        public UserMeResponse register(RegisterRequest request){

            if(userRepository.findByEmail(request.getEmail()).isPresent()){
                throw new EmailAlreadyExistsException("Email already exists");
            }

            UserEntity user = UserEntity.builder().name(request.getName()).email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).role(request.getRole()).build();
            UserEntity savedUser = userRepository.save(user);

            return userMapper.toUserMeResponse(savedUser);

        }

        public UserEntity findByEmail(String email){
            return userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found by email " + email));
        }


        //Here we taught Spring Security how to talk to our database so it can load user details during authentication.
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            // Convert Role Enum -> GrantedAuthority
            // Spring expects roles to look like "ROLE_CUSTOMER"
                List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                );

                return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    authorities // <--- Pass the real role here!
                );
        }


        
        // public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        //     UserEntity user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found with email"+email));
        
        //     return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),new ArrayList<>());
        // }


        // Get Partners (With Optional Filter)
        public List<PartnerSummary> getAllPartners(Boolean available){

            List<UserEntity> partners;

            if(available!=null){
                partners=userRepository.findAllByRoleAndAvailable(Role.PARTNER, available);
            }else{
                partners=userRepository.findAllByRole(Role.PARTNER);
            }

            List<OrderStatus> activeStatuses = List.of(OrderStatus.ASSIGNED,OrderStatus.ACCEPTED,OrderStatus.PICKED);

            List<PartnerSummary> response = new ArrayList<>();
        
            for(UserEntity p:partners){
                Long load = orderRepository.countByPartnerIdAndStatusIn(p.getId(), activeStatuses);

                response.add(PartnerSummary.builder().id(p.getId()).name(p.getName()).email(p.getEmail()).available(p.isAvailable()).currentLoad(load).build());
            }

            return response;
        
        }

}
