package com.crhistianm.springboot.gallo.springboot_gallo.shared.security;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.security.SpringSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.account.AccountUserDetailsService;

@ExtendWith(SpringExtension.class)
@Import(SpringSecurityConfig.class)
class SpringSecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    AuthenticationConfiguration aConfiguration;

    @MockitoBean
    AccountUserDetailsService accountUserDetailsService;

    @MockitoBean
    SecurityFilterChain securityFilterChain;



    //Just test the bean component as the service never returns password and shall not. 
    @Test
    void testEncriptPassword(){
        assertTrue(passwordEncoder.matches("12345", passwordEncoder.encode("12345")));
    }

}
