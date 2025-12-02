package com.example.oms.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.oms.user.entity.Role;
import com.example.oms.user.entity.UserEntity;
@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long>{
    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findAllByRoleAndAvailable(Role role, boolean available);
}


