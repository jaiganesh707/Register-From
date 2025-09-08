package com.example.RegisterService.Controller;


import com.example.RegisterService.Entity.RefreshToken;
import com.example.RegisterService.Entity.User;
import com.example.RegisterService.GlobalExceptionHandling.CustomException;
import com.example.RegisterService.Jwt.JwtUtils;
import com.example.RegisterService.Model.Enum.ERole;
import com.example.RegisterService.Model.Request.LoginRequest;
import com.example.RegisterService.Model.Request.TokenRefreshRequest;
import com.example.RegisterService.Model.Response.JwtResponse;
import com.example.RegisterService.Model.Response.RegisterResponse;
import com.example.RegisterService.Model.Response.TokenRefreshResponse;
import com.example.RegisterService.Model.Response.UserResponse;
import com.example.RegisterService.Repository.UserDao;

import com.example.RegisterService.Service.RefreshTokenService;
import com.example.RegisterService.Service.UserServiceImpl;
import com.example.RegisterService.UserDetailsConfig.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired AuthenticationManager authenticationManager;
    @Autowired private UserServiceImpl userService;
    @Autowired private UserDao userRepository;
    @Autowired private PasswordEncoder encoder;
    @Autowired JwtUtils jwtUtils;
    @Autowired RefreshTokenService refreshTokenService;

    public AuthController(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @PostMapping(value = "/signin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest)throws Exception {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String accessToken = jwtUtils.generateJwtToken(authentication);
        var refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(accessToken,
                refreshToken.getToken(),
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RegisterResponse<UserResponse>> registerUser(@Valid @RequestBody User signUpRequest)throws Exception {
        UserResponse savedUserResponse = userService.registerUser(signUpRequest);
        RegisterResponse<UserResponse> response = new RegisterResponse<>(
                LocalDateTime.now(),
                HttpStatus.CREATED.value(),
                "User registered successfully",
                savedUserResponse
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request)throws Exception {
        String refreshToken  = request.getRefreshToken();
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUserId)
                    .map(userId -> {
                        var userOpt = userRepository.findById(userId);
                        if (userOpt.isEmpty()) {
                            throw new RuntimeException("User not found for the refresh token");
                        }
                        String username = userOpt.get().getUsername();
                        Set<ERole> role=userOpt.get().getRoles();
                        String newAccessToken = jwtUtils.generateTokenFromUsername(username,role);
                        return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, refreshToken));
                    }).orElseThrow(() -> new RuntimeException("Refresh token not found. Please sign in again."));
    }

}
