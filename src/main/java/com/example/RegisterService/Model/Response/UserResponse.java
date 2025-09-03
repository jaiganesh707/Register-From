package com.example.RegisterService.Model.Response;

import com.example.RegisterService.Model.Enum.ERole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserResponse {
    private String userCode;
    private String username;
    private String email;
    @Enumerated(EnumType.STRING)
    private Set<ERole> roles = new HashSet<>();

    public UserResponse(String userCode, String username, String email, Set<ERole> roles) {
        this.userCode = userCode;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
