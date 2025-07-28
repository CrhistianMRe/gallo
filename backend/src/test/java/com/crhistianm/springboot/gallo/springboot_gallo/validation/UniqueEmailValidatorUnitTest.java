package com.crhistianm.springboot.gallo.springboot_gallo.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.service.AccountService;

@ExtendWith(MockitoExtension.class)
public class UniqueEmailValidatorUnitTest {

    @Mock
    private AccountService service;

    @InjectMocks
    private UniqueEmailValidator validator;
    
    @Test 
    void testInvalid(){
        when(service.isEmailAvailable(anyString())).thenReturn(false);
        assertFalse(validator.isValid("email@gmail.com", null));
    }

    @Test
    void testValid(){
        when(service.isEmailAvailable(anyString())).thenReturn(true);
        assertTrue(validator.isValid("email@gmail.com", null));
    }

}
