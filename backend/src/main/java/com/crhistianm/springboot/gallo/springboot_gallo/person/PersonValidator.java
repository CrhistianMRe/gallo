package com.crhistianm.springboot.gallo.springboot_gallo.person;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.account.IdentityVerificationService;


@Component
class PersonValidator {

    private final PersonValidationService personService;

    private final IdentityVerificationService identityService;

    PersonValidator(PersonValidationService personService, IdentityVerificationService identityService) {
        this.personService = personService;
        this.identityService = identityService;
    }

    void validateRequest(Long pathPersonId, PersonRequestDto personDto) {
        List<FieldInfoError> fields = new ArrayList<>();
        //If is null it is basically a create not update request so this validation is not activated
        identityService.validateUserAllowance(pathPersonId).ifPresent(fields::add);
        personService.validateUniquePhoneNumber(pathPersonId, personDto).ifPresent(fields::add);
        if(!fields.isEmpty()) throw new ValidationServiceException(fields);
    }


}
