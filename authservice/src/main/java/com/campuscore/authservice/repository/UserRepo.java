package com.campuscore.authservice.repository;

import com.campuscore.authservice.entity.AppUser;
import com.campuscore.authservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<AppUser,Integer> {

    Optional<AppUser> findByUserId(String userId);
    Optional<AppUser> findByEmail(String email, Role role);
    Optional<AppUser> findByUserID(String id);

    boolean existsByEmail(String email);
    long countByRole(Role role);
    boolean existsByUserId(String userId);
    boolean existsByRole(Role role);
}
