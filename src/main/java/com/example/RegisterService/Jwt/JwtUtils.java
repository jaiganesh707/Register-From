package com.example.RegisterService.Jwt;

import com.example.RegisterService.Model.Enum.ERole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${spring.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("roles", userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateTokenFromUsername(String username, Set<ERole> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles.stream()
                        .map(Enum::name)
                        .toList())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate Refresh Token
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh"); // mark this as refresh token
        return Jwts.builder()
                .setSubject(username)
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + refreshTokenDurationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public Set<ERole> getRolesFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody();

        List<String> roles = claims.get("roles", List.class);
        return roles.stream()
                .map(ERole::valueOf)  // Convert String -> Enum
                .collect(Collectors.toSet());
    }

    public boolean validateJwtToken(String authToken) {
        try {
            if (authToken == null || authToken.trim().isEmpty()) {
                logger.error("JWT token is null or empty");
                return false;
            }

            // Check for invalid characters
            if (authToken.contains("\\") || authToken.contains(" ") || authToken.contains("\"")) {
                logger.error("JWT token contains invalid characters");
                return false;
            }
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        }catch (SecurityException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        }  catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }catch (Exception e) {
            logger.error("Unexpected error during token validation: {}", e.getMessage());
        }
        return false;
    }


    private Key key() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret); // use BASE64URL if your value uses '-' or '_'
        return Keys.hmacShaKeyFor(keyBytes); // length is correct because we generated it
    }
}
