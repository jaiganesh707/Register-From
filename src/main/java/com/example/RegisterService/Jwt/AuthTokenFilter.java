package com.example.RegisterService.Jwt;

import com.example.RegisterService.GlobalExceptionHandling.CustomException;
import com.example.RegisterService.Model.Enum.ERole;
import com.example.RegisterService.Model.Response.RegisterResponse;
import com.example.RegisterService.Repository.UserDao;
import com.example.RegisterService.UserDetailsConfig.UserDetailsImpl;
import com.example.RegisterService.UserDetailsConfig.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenFilter.class);
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private UserDao userDao;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String path=request.getServletPath();
            if (path.startsWith("/api/auth/")) {
                filterChain.doFilter(request, response);
                return;
            }
            String header=request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                if (jwtUtils.validateJwtToken(token)) {
                    String username = jwtUtils.getUserNameFromJwtToken(token);
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetailsImpl userDetails =
                                (UserDetailsImpl) userDetailsService.loadUserByUsername(username);

                            UsernamePasswordAuthenticationToken authToken =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities()
                                    );
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                            LOGGER.info("Authenticated user: {}", username);
                        }
                    } else {
                    LOGGER.warn("Invalid/Expired JWT token");
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Invalid or expired JWT token\"}");
                    return;
                }
            } else {
                LOGGER.debug("No JWT token in Authorization header");
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            LOGGER.error("AuthTokenFilter Error: {}", ex.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
        }
    }
    private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        RegisterResponse<Object> errorResponse = new RegisterResponse<>(
                LocalDateTime.now(),
                status.value(),
                message,
                null
        );

        response.setStatus(status.value());
        response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }

        public UserDetailsImpl validateAndGetUser(String header) {
            if (header == null || !header.startsWith("Bearer ")) {
                throw new CustomException("TOKEN-MISSING or INVALID FORMAT",HttpStatus.UNAUTHORIZED.value());
            }

            String token = header.substring(7);

            if (!jwtUtils.validateJwtToken(token)) {
                throw new CustomException("TOKEN-INVALID or EXPIRED",HttpStatus.UNAUTHORIZED.value());
            }

            String username = jwtUtils.getUserNameFromJwtToken(token);
            Set<ERole> roles = jwtUtils.getRolesFromJwtToken(token); // you need this method
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority(role.name())) // convert enum to String
                    .toList();

            UserDetailsImpl userDetails = new UserDetailsImpl(
                    null,  // id not needed for authentication here
                    username,
                    "",    // email not needed
                    "",    // password not needed
                    authorities
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            if (username == null) {
                throw new CustomException("USERNAME-NOT-FOUND-IN-TOKEN",HttpStatus.UNAUTHORIZED.value());
            }

            return (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
        }
    }
