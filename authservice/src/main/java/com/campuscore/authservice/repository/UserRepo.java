package com.campuscore.authservice.repository;

import com.campuscore.authservice.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<AppUser,Integer> {

    Optional<AppUser> findByUserId(String userId);
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
}
