package com.crhistianm.springboot.gallo.springboot_gallo.validation.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.service.AccountValidationService;
import com.crhistianm.springboot.gallo.springboot_gallo.service.IdentityVerificationService;
import com.crhistianm.springboot.gallo.springboot_gallo.service.PersonValidationService;

@Component
public class AccountValidator {

    private final AccountValidationService accountService;

    private final PersonValidationService personService;

    private final IdentityVerificationService identityService;

    public AccountValidator(AccountValidationService accountService, PersonValidationService personService, IdentityVerificationService identityService) {
        this.accountService = accountService;
        this.personService = personService;
        this.identityService = identityService;
    }

    public void validateRequest(AccountRequestDto accountDto) {
        List<FieldInfoError> fields = new ArrayList<>();
        personService.validatePersonRegistered(accountDto).ifPresent(fields::add);
        //Null id as is account creation
        accountService.validatePersonAssigned(accountDto).ifPresent(fields::add);
        //Null id as is account creation
        accountService.validateUniqueEmail(null, accountDto).ifPresent(fields::add);

        identityService.validateAdminRequired(accountDto, accountDto.isAdmin()).ifPresent(fields::add);

        if(!fields.isEmpty()) throw new ValidationServiceException(fields);
    }

}
