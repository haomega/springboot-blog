package com.example.blog.config;

import com.example.blog.entity.User;
import com.example.blog.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MyUserDetailsService implements UserDetailsService {
    private Map<String, User> map = new ConcurrentHashMap<>();

    private final PasswordEncoder passwordEncoder;
    private UserMapper userMapper;

    public MyUserDetailsService(PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public User save(String username, String password, String avatar) {
        userMapper.saveUser(username, passwordEncoder.encode(password), avatar);
        return getUserByName(username);
    }

    public User getUserByName(String username) {
        return userMapper.getUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("username not found");
        }
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), Collections.emptyList());
    }
}
