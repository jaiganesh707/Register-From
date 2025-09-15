package com.example.RegisterService.Model.Response;

import com.example.RegisterService.Model.Enum.ERole;
import lombok.NoArgsConstructor;

import java.util.Set;
@NoArgsConstructor
public class UserResponse {
    private String userCode;
    private String username;
    private String email;
    private Set<String> roles;

    public UserResponse(String userCode, String username, String email, Set<String> roles) {
        this.userCode = userCode;
        this.username = username;
        this.email = email;
        this.roles=roles;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
