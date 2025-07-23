package com.crhistianm.springboot.gallo.springboot_gallo.validation;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.service.AccountService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String>{

    private final AccountService accountService;

    public UniqueEmailValidator(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext arg1) {
        if(email == null) return true;
        return accountService.isEmailAvailable(email);
    }

    
    
}
