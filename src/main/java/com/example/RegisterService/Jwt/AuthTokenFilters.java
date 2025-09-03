package com.example.RegisterService.Jwt;

import com.example.RegisterService.Repository.UserDao;
import com.example.RegisterService.UserDetailsConfig.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthTokenFilters implements TokenValidate{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenFilter.class);
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private UserDao userDao;

    private boolean isBearerToken(String token) {
        return token != null && token.startsWith("Bearer ");
    }

    private String extractJwtFromBearer(String token) {
        return token.substring(7);
    }
    @Override
    public boolean isValidToken(String token) {
        try {
            if (isBearerToken(token)) {
                String jwtToken = extractJwtFromBearer(token);
                return jwtUtils.validateJwtToken(jwtToken);
            }
        } catch (Exception e) {
            // Log the error if needed
        }
        return false;
    }
}
