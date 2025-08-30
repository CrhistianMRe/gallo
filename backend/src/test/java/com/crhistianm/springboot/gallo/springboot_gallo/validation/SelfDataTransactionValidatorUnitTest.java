package com.crhistianm.springboot.gallo.springboot_gallo.validation;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;

import org.apache.commons.lang3.LongRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.service.IdentityVerificationServiceImpl;

import jakarta.validation.Payload;

@ExtendWith(MockitoExtension.class)
public class SelfDataTransactionValidatorUnitTest {

    @Mock
    private IdentityVerificationServiceImpl service;

    @InjectMocks
    private SelfDataTransactionValidator validator;

    private static SelfDataTransaction givenSelfDataTransactionAnnotation(Class<?> targetClass){

        SelfDataTransaction constraintAnnotation = new SelfDataTransaction() {
            @Override
            public Class<?>[] groups() {
                return null;
            }

            @Override
            public String message() {
                return null;
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return null;
            }

            @Override
            public Class<?> targetClass() {
                return targetClass;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }
        };

        return constraintAnnotation;
    }

    @BeforeEach
    void setUp(){
        lenient().when(service.isAdminAuthority()).thenReturn(false);
        lenient().when(service.isUserPersonEntityAllowed((anyLong()))).thenAnswer(invo ->{
            boolean answer = false;
            if(invo.getArgument(0, Long.class) == 1) answer = true;
            return answer;
        });
    }

    @Test
    void testInvalid(){
       validator.initialize(givenSelfDataTransactionAnnotation(Person.class));
        assertFalse(validator.isValid(2L, null));
    }

    @Test
    void testInitialize(){
       validator.initialize(givenSelfDataTransactionAnnotation(Object.class));
       assertEquals(Object.class, validator.targetClass);
    }

    @Test
    void testValidAdminAuthority(){
        when(service.isAdminAuthority()).thenReturn(true);
        assertTrue(validator.isValid(10L, null));
    }

    @Test
    void testNull(){
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testValid(){
        validator.initialize(givenSelfDataTransactionAnnotation(Person.class));
        assertTrue(validator.isValid(1L, null));
    }
    
}
