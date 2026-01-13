package com.crhistianm.springboot.gallo.springboot_gallo.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CorrectGenderValidatorUnitTest {

    private CorrectGenderValidator validator;


    @BeforeEach
    void setUp(){
        this.validator = new CorrectGenderValidator();
    }

    @Test
    void testInvalid(){
        assertFalse(validator.isValid("MALE", null));
    }

    @Test
    void testValid(){
        assertTrue(validator.isValid("M", null));
        assertTrue(validator.isValid("F", null));
        assertTrue(validator.isValid("NT", null));
    }

}
