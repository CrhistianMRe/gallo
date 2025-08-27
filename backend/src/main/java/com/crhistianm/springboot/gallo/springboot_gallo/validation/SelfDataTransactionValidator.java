package com.crhistianm.springboot.gallo.springboot_gallo.validation;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.service.IdentityVerificationService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class SelfDataTransactionValidator implements ConstraintValidator<SelfDataTransaction, Long> {

    private final IdentityVerificationService identityService;

    public SelfDataTransactionValidator(IdentityVerificationService identityVerificationService) {
        this.identityService = identityVerificationService;
    }

    public Class<?> targetClass;

    @Override
    public void initialize(SelfDataTransaction constraintAnnotation) {
        targetClass = constraintAnnotation.targetClass();
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context) {
        if(id == null || identityService.isAdminAuthority()) return true;
        if(targetClass.equals(Person.class)) return identityService.isUserPersonEntityAllowed(id);
        return false;
    }
    
}
