package com.crhistianm.springboot.gallo.springboot_gallo.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.service.PersonService;

@ExtendWith(MockitoExtension.class)
public class PersonRegisteredValidatorUnitTest {

    @Mock
    private PersonService service;
    
    @InjectMocks
    private PersonRegisteredValidator validator;

    @Test
    void testInvalid(){
        when(service.isPersonRegistered(anyLong())).thenReturn(false);
        assertFalse(validator.isValid(1L, null));
    }
    
    @Test
    void testValid(){
        when(service.isPersonRegistered(anyLong())).thenReturn(true);
        assertTrue(validator.isValid(1L, null));
    }
}
