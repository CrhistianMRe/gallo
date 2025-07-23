package com.crhistianm.springboot.gallo.springboot_gallo.validation;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class CorrectGenderValidator implements ConstraintValidator<CorrectGender, String>{

    @Override
    public boolean isValid(String gender, ConstraintValidatorContext arg1) {
        if(gender.equals("M") || gender.equals("F") || gender.equals("NT")){
            return true;
        }
        return false;
    }
    
}
