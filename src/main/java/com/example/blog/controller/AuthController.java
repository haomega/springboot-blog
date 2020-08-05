package com.example.blog.controller;

import com.example.blog.config.MyUserDetailsService;
import com.example.blog.entity.User;
import com.example.blog.entity.Result;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("auth")
public class AuthController {

    private static final Pattern USERNAME_WORD_UNDERSCORE = Pattern.compile("[\\w_]+");

    MyUserDetailsService userDetailsService;
    AuthenticationManager authenticationManager;

    @Inject
    public AuthController(MyUserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public Object register(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String password = requestBody.get("password");

        if (username == null || password == null) {
            return Result.failure("用户名/密码 不能为空");
        }

        if (username.length() < 1 || username.length() > 15) {
            return Result.failure("username 长度应为 1-15");
        }

        if (!USERNAME_WORD_UNDERSCORE.matcher(username).find()) {
            return Result.failure("username 只能是字母/下划线/中文");
        }

        if (password.length() < 6 || password.length() > 16) {
            return Result.failure("password 长度应为 6-16");
        }

        User user = userDetailsService.save(username, password, "");
        return Result.success("注册成功", user);
    }

    @PostMapping("/login")
    public Object login(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String password = requestBody.get("password");

        User user = userDetailsService.getUserByName(username);
        if (user == null) {
            return Result.failure("用户不存在");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        // 与提交的用户名密码比较
        try {
            Authentication authenticate = authenticationManager.authenticate(token);
            // auth ok
            SecurityContextHolder.getContext().setAuthentication(authenticate);
            return Result.success("登录成功", user);
        } catch (BadCredentialsException e) {

        }
        return Result.failure("密码错误");
    }

    // 判断用户是否登录
    @GetMapping
    public Object auth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userDetailsService.getUserByName(authentication == null ? null : authentication.getName());
        if (user == null) {
            return Result.success(false);
        } else {
            return Result.success(true, user);
        }
    }

    @GetMapping("/logout")
    public Object logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userDetailsService.getUserByName(username) != null) {
            SecurityContextHolder.clearContext();
            return Result.success("注销成功");
        }else {
            return Result.failure("用户尚未登录");
        }
    }


}
