package com.campuscore.authservice.repository;

import com.campuscore.authservice.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepo extends JpaRepository<UserToken,String> {

}
