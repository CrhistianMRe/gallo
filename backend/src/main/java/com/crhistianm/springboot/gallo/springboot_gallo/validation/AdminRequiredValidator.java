package com.crhistianm.springboot.gallo.springboot_gallo.validation;


import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AdminRequiredValidator implements ConstraintValidator<AdminRequired, Boolean>{



    @Override
    public boolean isValid(Boolean value, ConstraintValidatorContext context) {

        Boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        if ((value == null || value == false) || isAdmin) return true;

        return false;
    }
    
}
