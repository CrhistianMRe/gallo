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

import com.crhistianm.springboot.gallo.springboot_gallo.service.PersonService;

@ExtendWith(MockitoExtension.class)
public class UniquePhoneNumberValidatorUnitTest {

    @Mock
    private PersonService service;

    @InjectMocks
    private UniquePhoneNumberValidator validator;

    @Test
    void testInvalid(){
        when(service.isPhoneNumberAvailable(anyString())).thenReturn(false);
        assertFalse(validator.isValid("222111222",null));
    }


    @Test
    void testValid(){
        when(service.isPhoneNumberAvailable(anyString())).thenReturn(true);
        assertTrue(validator.isValid("222111222",null));
    }
    

    

}
