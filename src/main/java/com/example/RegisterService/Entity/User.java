package com.example.RegisterService.Entity;

import com.example.RegisterService.Model.Enum.ERole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment
    private Long id;
    @Column(unique = true, nullable = false, length = 12)
    private String userCode;
    @NotBlank(message = "Username is mandatory")
    @Size(max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String username;

    @NotBlank(message = "Email is mandatory")
    @Email
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<ERole> roles = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<ERole> getRoles() {
        return roles;
    }

    public void setRoles(Set<ERole> roles) {
        this.roles = roles;
    }

}
