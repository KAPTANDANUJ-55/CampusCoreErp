package com.campuscore.authservice.repository;

import com.campuscore.authservice.entity.AppUser;
import com.campuscore.authservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<AppUser,Integer> {

    Optional<AppUser> findByUserId(String userId, Role role);

    boolean existsByEmail(String email);
    long countByRole(Role role);

    boolean existsByRole(Role role);
}
