package com.example.RegisterService.Controller;

import com.example.RegisterService.Entity.User;
import com.example.RegisterService.GlobalExceptionHandling.CustomException;
import com.example.RegisterService.Jwt.AuthTokenFilter;
import com.example.RegisterService.Model.Response.RegisterResponse;
import com.example.RegisterService.Service.UserServiceImpl;
import com.example.RegisterService.UserDetailsConfig.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
@RestController
@RequestMapping("/user")
public class UserController {
@Autowired private UserServiceImpl userService;
@Autowired private AuthTokenFilter authTokenFilter;

    @GetMapping("/")
    public ResponseEntity<?> userList(@RequestHeader(value = "Authorization", required = false) String authHeader)throws Exception{
        try {
            UserDetailsImpl userDetails = authTokenFilter.validateAndGetUser(authHeader);
            // You can now validate user info if needed:
            String username = userDetails.getUsername();
            System.out.println("Logged-in user: " + username);

            // If valid → fetch user list
            List<User> list = userService.registerList();
            RegisterResponse<List<User>> response = new RegisterResponse<>(
                    LocalDateTime.now(),
                    HttpStatus.OK.value(),
                    "User list fetched successfully",
                    list
            );
            return ResponseEntity.ok(response);

        } catch (CustomException ex) {
            RegisterResponse<List<User>> response = new RegisterResponse<>(
                    LocalDateTime.now(),
                    ex.getStatus(),
                    ex.getMessage(),
                    null
            );
            return ResponseEntity.status(ex.getStatus()).body(response);
        }
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<RegisterResponse<User>> getUser(@RequestHeader(value = "Authorization", required = false) String authHeader,@Valid @PathVariable Long id)throws Exception{
        try {
        authTokenFilter.validateAndGetUser(authHeader);
        User user=userService.findUser(id);
        RegisterResponse<User> response=new RegisterResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "User fetched successfully",
                user
        );
        return ResponseEntity.ok(response);
    }catch (CustomException e){
            RegisterResponse<User> response=new RegisterResponse<>(
                    LocalDateTime.now(),
                    e.getStatus(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(e.getStatus()).body(response);
        }
    }
    @PostMapping("/delete/{id}")
    @PreAuthorize(("hasRole('ROLE_ADMIN')"))
    public ResponseEntity<RegisterResponse> deleteUser(@RequestHeader(value = "Authorization", required = false) String authHeader,@Valid @PathVariable Long id)throws Exception{
        authTokenFilter.validateAndGetUser(authHeader);
        String message= userService.deleteUser(id);
        RegisterResponse<User> response=new RegisterResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                message,
                null
        );
        return ResponseEntity.ok(response);
    }

}
