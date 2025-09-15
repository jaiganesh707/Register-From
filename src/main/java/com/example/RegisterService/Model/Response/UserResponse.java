package com.example.RegisterService.Model.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String userCode;
    private String username;
    private String email;
    private Set<String> roles;

    public UserResponse(String userCode, String username, String email, Set<String> userRole) {
    }
}
