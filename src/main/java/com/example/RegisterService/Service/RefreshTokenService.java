package com.example.RegisterService.Service;

import com.example.RegisterService.Entity.RefreshToken;
import com.example.RegisterService.Repository.RefreshTokenDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${spring.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenDao refreshTokenRepository;

    public RefreshTokenService(RefreshTokenDao refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long id) {
        return refreshTokenRepository.findByUserId(id)
                .map(existingToken -> {
                    existingToken.setToken(UUID.randomUUID().toString());
                    existingToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
                    return refreshTokenRepository.save(existingToken);
                })
                .orElseGet(() -> {
                    RefreshToken newToken = new RefreshToken();
                    newToken.setUserId(id);
                    newToken.setToken(UUID.randomUUID().toString());
                    newToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
                    return refreshTokenRepository.save(newToken);
                });
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please sign in again.");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUserId(userId);
    }
}
