package com.crhistianm.springboot.gallo.springboot_gallo.validation;


import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.service.IdentityVerificationService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class AdminRequiredValidator implements ConstraintValidator<AdminRequired, Boolean>{

    private final IdentityVerificationService service;

    public AdminRequiredValidator(IdentityVerificationService service) {
        this.service = service;
    }

    @Override
    public boolean isValid(Boolean value, ConstraintValidatorContext context) {
        Boolean isAdmin = false; 
        isAdmin = service.isAdminAuthority();
        
        if ((value == null || value == false) || isAdmin) return true;

        return false;
    }
    
}
