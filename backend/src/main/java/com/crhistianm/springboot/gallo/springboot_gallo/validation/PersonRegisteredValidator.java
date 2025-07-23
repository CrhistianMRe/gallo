package com.crhistianm.springboot.gallo.springboot_gallo.validation;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.service.PersonService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class PersonRegisteredValidator implements ConstraintValidator<PersonRegistered, Long>{

    private final PersonService personService;

    public PersonRegisteredValidator(PersonService personService){
        this.personService = personService;
    }

    @Override
    public boolean isValid(Long personId, ConstraintValidatorContext arg1) {
        if(personId == null) return true;
        return personService.isPersonRegistered(personId);
    }
}
    
