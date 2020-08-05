package com.example.blog.controller;

import com.alibaba.fastjson.JSON;
import com.example.blog.config.MyUserDetailsService;
import com.example.blog.entity.User;
import jdk.net.SocketFlow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static jdk.net.SocketFlow.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class AuthControllerTest {
    private MockMvc mvc;
    @Mock
    private MyUserDetailsService userDetailsService;
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    Authentication authentication;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(userDetailsService, authenticationManager)).build();
    }

    @Test
    void notLoginAuth() throws Exception {

        mvc.perform(get("/auth")).andExpect(status().isOk()).andExpect(mvcResult -> {
            String response = mvcResult.getResponse().getContentAsString();
            assertTrue(response.contains("false"));
        });
    }

    @Test
    void registerAndLoginAuth() throws Exception {
        // register
        Map<String, String> usernamePassword = new HashMap<>();
        usernamePassword.put("username", "myUser");
        usernamePassword.put("password", "myPassword");

        MockHttpServletRequestBuilder registerPost = post("/auth/register")
                .header("Content-Type", "application/json", "charset=UTF-8")
                .content(JSON.toJSONString(usernamePassword));
        mvc.perform(registerPost).andExpect(status().isOk()).andExpect(mvcResult -> {
            System.out.println(mvcResult.getResponse().getContentAsString());
            assertTrue(mvcResult.getResponse().getContentAsString().contains("ok"));
        });
        // login
        Mockito.when(userDetailsService.getUserByName("myUser")).thenReturn(new User(1, "myUser", passwordEncoder.encode("myPassword")));
        Mockito.when(userDetailsService.loadUserByUsername("myUser")).thenReturn(new org.springframework.security.core.userdetails.User("myUser", passwordEncoder.encode("myPassword"), Collections.emptyList()));
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("myUser");
        MvcResult responseResult = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(JSON.toJSONString(usernamePassword)))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    System.out.println(mvcResult.getResponse().getContentAsString());
                    assertTrue(mvcResult.getResponse().getContentAsString().contains("ok"));
                }).andReturn();

        HttpSession session = responseResult.getRequest().getSession();
        // auth with login
        Mockito.when(userDetailsService.getUserByName("myUser")).thenReturn(new User(1, "myUser", passwordEncoder.encode("myPassword")));
        mvc.perform(get("/auth").session((MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
            String response = mvcResult.getResponse().getContentAsString();
            System.out.println(response);
            assertTrue(response.contains("myUser"));
        });
    }


}