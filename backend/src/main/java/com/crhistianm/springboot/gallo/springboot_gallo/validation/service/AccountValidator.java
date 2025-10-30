package com.crhistianm.springboot.gallo.springboot_gallo.validation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountUpdateRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.service.AccountValidationService;
import com.crhistianm.springboot.gallo.springboot_gallo.service.IdentityVerificationService;
import com.crhistianm.springboot.gallo.springboot_gallo.service.PersonValidationService;
import com.crhistianm.springboot.gallo.springboot_gallo.service.RoleValidationService;

@Component
public class AccountValidator {

    private final AccountValidationService accountService;

    private final PersonValidationService personService;

    private final IdentityVerificationService identityService;

    private final RoleValidationService roleService;

    public AccountValidator(AccountValidationService accountService, PersonValidationService personService, IdentityVerificationService identityService, RoleValidationService roleService) {
        this.accountService = accountService;
        this.personService = personService;
        this.identityService = identityService;
        this.roleService = roleService;
    }

    public void validateRequest(AccountRequestDto accountDto) {
        List<FieldInfoError> fields = new ArrayList<>();
        personService.validatePersonRegistered(accountDto).ifPresent(fields::add);
        //Null id as is account creation
        accountService.validatePersonAssigned(null, accountDto).ifPresent(fields::add);
        //Null id as is account creation
        accountService.validateUniqueEmail(null, accountDto).ifPresent(fields::add);

        if(accountDto.isAdmin()){
            identityService.validateAdminRequired(accountDto, "admin").ifPresent(fields::add);
        }

        if(!fields.isEmpty()) throw new ValidationServiceException(fields);
    }

    public void validateUpdateRequest(Long accountPathId, AccountUpdateRequestDto accountDto, Long personId) {
        List<FieldInfoError> fields = new ArrayList<>();

        identityService.validateUserAllowance(personId).ifPresent(fields::add);

        if(accountDto.getPersonId() != null){ 
            personService.validatePersonRegistered(accountDto).ifPresent(fields::add);
            accountService.validatePersonAssigned(accountPathId, accountDto).ifPresent(fields::add);
            identityService.validateAdminRequired(accountDto, "personId").ifPresent(fields::add);
        }
        if(accountDto.getEmail() != null) accountService.validateUniqueEmail(accountPathId, accountDto).ifPresent(fields::add);
        if(accountDto.isEnabled() != null) identityService.validateAdminRequired(accountDto, "enabled").ifPresent(fields::add);
        if(!accountDto.getRoles().isEmpty()) {
            identityService.validateAdminRequired(accountDto, "roles").ifPresent(fields::add);
            fields.addAll(accountDto.getRoles().stream().flatMap(role -> roleService.validateRoleExists(role).stream()).toList());
        }

        if(!fields.isEmpty()) throw new ValidationServiceException(fields);
    }

}
