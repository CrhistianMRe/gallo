package com.crhistianm.springboot.gallo.springboot_gallo.person;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class CorrectGenderValidator implements ConstraintValidator<CorrectGender, String>{

    @Override
    public boolean isValid(String gender, ConstraintValidatorContext arg1) {
        if(gender == null) return true;
        if(gender.equals("M") || gender.equals("F") || gender.equals("NT")){
            return true;
        }
        return false;
    }
    
}
