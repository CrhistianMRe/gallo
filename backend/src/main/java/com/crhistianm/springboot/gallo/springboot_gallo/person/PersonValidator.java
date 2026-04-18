package com.crhistianm.springboot.gallo.springboot_gallo.person;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.ValidationServiceErrorList;
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
        ValidationServiceErrorList errorList = new ValidationServiceErrorList();
        //If is null it is basically a create not update request so this validation is not activated
        identityService.validateUserAllowanceByPersonId(pathPersonId).ifPresent(errorList::add);
        personService.validateUniquePhoneNumber(pathPersonId, personDto).ifPresent(errorList::add);

        errorList.throwFieldErrors();
    }


}
