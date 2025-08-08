package com.crhistianm.springboot.gallo.springboot_gallo.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class AdminRequiredValidatorUnitTest {

    private AdminRequiredValidator validator;

    @BeforeEach
    void setUp(){
        this.validator = new AdminRequiredValidator();
    }

    private void setSampleAuth(Collection<? extends GrantedAuthority> authorities){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("example@gmail.com", null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    void testInvalid(){
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER")); 
        setSampleAuth(authorities);
        assertFalse(validator.isValid(true, null));
    }

    @Nested
    class ValidTest{

        @Test
        void testFalse(){
            assertTrue(validator.isValid(false, null));
        }

        @Test
        void testNull(){
            assertTrue(validator.isValid(null, null));
        }

        @Test
        void testContext(){
            Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"),new SimpleGrantedAuthority("ROLE_ADMIN")); 
            setSampleAuth(authorities);

            assertTrue(validator.isValid(true, null));

            SecurityContextHolder.clearContext();
        }

    }
    
    
}
