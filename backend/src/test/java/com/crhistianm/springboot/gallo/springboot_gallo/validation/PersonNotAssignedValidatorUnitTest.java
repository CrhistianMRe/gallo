package com.crhistianm.springboot.gallo.springboot_gallo.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.service.AccountService;

@ExtendWith(MockitoExtension.class)
public class PersonNotAssignedValidatorUnitTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private PersonNotAssignedValidator validator;

    @Test
    void testValid(){
        when(accountService.isPersonIdAssigned(1L)).thenReturn(true);
        assertFalse(validator.isValid(1L, null));
    }

    @Test
    void testInvalid(){
        when(accountService.isPersonIdAssigned(2L)).thenReturn(false);
        assertTrue(validator.isValid(2L, null));
    }
    
}

