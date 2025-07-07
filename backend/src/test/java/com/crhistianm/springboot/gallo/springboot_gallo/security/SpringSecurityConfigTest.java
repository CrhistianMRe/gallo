package com.crhistianm.springboot.gallo.springboot_gallo.security;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class SpringSecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    //Just test the bean component as the service never returns password and shall not. 
    @Test
    void testEncriptPassword(){
        assertTrue(passwordEncoder.matches("12345", passwordEncoder.encode("12345")));
    }

}
