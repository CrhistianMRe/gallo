package com.crhistianm.springboot.gallo.springboot_gallo.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.service.AccountService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String>{

    @Autowired
    private AccountService accountService;


    @Override
    public boolean isValid(String email, ConstraintValidatorContext arg1) {
        return !accountService.isEmailAvailable(email);
    }

    
    
}
