package com.crhistianm.springboot.gallo.springboot_gallo.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.service.AccountService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class PersonNotAssignedValidator implements ConstraintValidator<PersonNotAssigned, Long>{

    @Autowired
    private AccountService accountService;

    @Override
    public boolean isValid(Long personId, ConstraintValidatorContext arg1) {
        //If exists is not valid as is already used
        return !accountService.isPersonIdAssigned(personId);
    }
    
}
