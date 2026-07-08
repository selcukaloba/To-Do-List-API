package com.selcukaloba.to_do_api_project.repository;

import com.selcukaloba.to_do_api_project.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
