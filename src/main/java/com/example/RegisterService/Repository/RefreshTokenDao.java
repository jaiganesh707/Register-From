package com.example.RegisterService.Repository;

import com.example.RegisterService.Entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenDao extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserId(Long userId);
    int deleteByUserId(Long userId);
}
