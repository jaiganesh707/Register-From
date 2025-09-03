package com.example.RegisterService.UserDetailsConfig;

import com.example.RegisterService.Entity.User;
import com.example.RegisterService.Repository.UserDao;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDao userDao;

    @Autowired
    public UserDetailsServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return UserDetailsImpl.build(u);
    }
}
