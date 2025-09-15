package com.example.RegisterService.Service;

import com.example.RegisterService.Entity.User;
import com.example.RegisterService.GlobalExceptionHandling.CustomException;
import com.example.RegisterService.Model.Enum.ERole;
import com.example.RegisterService.Model.Response.RegisterResponse;
import com.example.RegisterService.Repository.UserDao;
import com.example.RegisterService.Security.UniqueIdGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl {
    @Autowired
    private UserDao userDao;
    @Autowired private PasswordEncoder encoder;


    @Transactional
    public Object registerUser(User request){
        if (Boolean.TRUE.equals(userDao.existsByUsername(request.getUsername()))) {
            throw new CustomException("Error: Username already exists", 400);
        }
        if (Boolean.TRUE.equals(userDao.existsByEmail(request.getEmail()))) {
            throw new CustomException("Error: Email already exists", 400);
        }
        // Validate request fields
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new CustomException("Error: Password cannot be empty", 400);
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRoles(new HashSet<>());
        user.getRoles().add(ERole.ROLE_USER);
        user.setUserCode(UniqueIdGenerator.generateUserCode(8));
        User savedUser = userDao.save(user);
        return savedUser;
    }

    public List<User> registerList()throws Exception{
        return userDao.findAll();
    }

    public User findUser(Long id)throws Exception{
        return userDao.findById(id)
                .orElseThrow(() -> new CustomException("User not found with id: " + id, 404));
    }

    public String deleteUser(Long id)throws Exception{
        Optional<User> userData=userDao.findById(id);
        if(userData.isEmpty()){
            throw new CustomException("Error: User not found with id: " + id, 404);}
        userDao.deleteById(id);
        return "Deleted the user "+userData.get().getUsername();
    }
}
