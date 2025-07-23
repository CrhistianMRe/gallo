package com.crhistianm.springboot.gallo.springboot_gallo.validation;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.service.PersonService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UniquePhoneNumberValidator implements ConstraintValidator<UniquePhoneNumber, String>{

    private final PersonService personService;

    public UniquePhoneNumberValidator(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext arg1) {
        return !personService.isPhoneNumberAvailable(phoneNumber);
    }
    
}
