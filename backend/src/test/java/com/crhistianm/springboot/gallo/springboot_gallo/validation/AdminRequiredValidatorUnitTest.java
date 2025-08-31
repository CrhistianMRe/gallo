package com.crhistianm.springboot.gallo.springboot_gallo.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.crhistianm.springboot.gallo.springboot_gallo.service.IdentityVerificationServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AdminRequiredValidatorUnitTest {

    @InjectMocks
    private AdminRequiredValidator validator;

    @Mock
    private IdentityVerificationServiceImpl serviceImpl;

    @Test
    void testInvalid(){
        when(serviceImpl.isAdminAuthority()).thenReturn(false);
        assertFalse(validator.isValid(true, null));
    }

    @Nested
    class ValidTest{

        @Test
        void testFalseInput(){
            assertTrue(validator.isValid(false, null));
        }

        @Test
        void testNullInput(){
            assertTrue(validator.isValid(null, null));
        }

        @Test
        void testValidContext(){
            when(serviceImpl.isAdminAuthority()).thenReturn(true);
            assertTrue(validator.isValid(true, null));
        }


    }
    
    
}
