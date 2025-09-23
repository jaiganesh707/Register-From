package com.example.RegisterService.Service;

import com.example.RegisterService.Entity.User;
import com.example.RegisterService.GlobalExceptionHandling.CustomException;
import com.example.RegisterService.Model.Enum.ERole;
import com.example.RegisterService.Model.Response.UserResponse;
import com.example.RegisterService.Repository.UserDao;
import com.example.RegisterService.Security.UniqueIdGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        Set<ERole> roleEntities;
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            roleEntities = Set.of(ERole.ROLE_GUEST);
        }else {
            roleEntities = request.getRoles().stream()
                    .map(roleStr -> {
                        try {
                            return ERole.valueOf(String.valueOf(roleStr));
                        } catch (IllegalArgumentException e) {
                            throw new CustomException("Error: Invalid role " + roleStr, 400);
                        }
                    })
                    .collect(Collectors.toSet());
        }
        user.setRoles(roleEntities);

        user.setUserCode(UniqueIdGenerator.generateUserCode(8));
        User userData=userDao.save(user);
        Set<String> roleNames = userData.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
        return new UserResponse(userData.getUserCode(),userData.getUsername(),userData.getEmail(),roleNames);
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
