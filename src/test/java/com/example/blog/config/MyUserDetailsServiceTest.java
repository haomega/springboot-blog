package com.example.blog.config;

import com.example.blog.entity.User;
import com.example.blog.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.annotation.Testable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    public PasswordEncoder passwordEncoder;
    @InjectMocks
    private MyUserDetailsService myUserDetailsService;

    @Test
    public void saveTest() {
        Mockito.when(passwordEncoder.encode("myPassword")).thenReturn("myEncodedPassword");
        Mockito.when(myUserDetailsService.getUserByName("myUser")).thenReturn(new User(1, "myUser", "myEncodedPassword"));

        User user = myUserDetailsService.save("myUser", "myPassword", "");
        Mockito.verify(userMapper).saveUser("myUser", "myEncodedPassword", "");
        assertEquals(1, user.getId());

    }

    @Test
    public void getUserByUsernameTest() {
        // user not found
        assertNull(myUserDetailsService.getUserByName("myUser"));
        // user found
        Mockito.when(userMapper.getUserByUsername("myUser")).thenReturn(new User(1, "myUser", "myEncodedPassword"));

        User myUser = myUserDetailsService.getUserByName("myUser");

        assertEquals(1, myUser.getId());
        assertEquals("myUser", myUser.getUsername());
        assertEquals("myEncodedPassword", myUser.getPassword());

    }

    @Test
    public void loadUserByUsernameWhenUserNullTest() {
        assertThrows(UsernameNotFoundException.class, () -> myUserDetailsService.loadUserByUsername("myUser"));
    }

    @Test
    public void loadUserByUsernameTest() {
        Mockito.when(myUserDetailsService.getUserByName("myUser")).thenReturn(new User(1, "myUser", "myEncodedPassword"));
        UserDetails userDetails = myUserDetailsService.loadUserByUsername("myUser");
        assertEquals("myUser", userDetails.getUsername());
        assertEquals("myEncodedPassword", userDetails.getPassword());
    }

}