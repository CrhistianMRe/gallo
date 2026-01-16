package com.crhistianm.springboot.gallo.springboot_gallo.account;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.person.PersonValidationService;

@Component
class AccountValidator {

    private final AccountValidationService accountService;

    private final PersonValidationService personService;

    private final IdentityVerificationService identityService;

    private final RoleValidationService roleService;

    AccountValidator(AccountValidationService accountService, PersonValidationService personService, IdentityVerificationService identityService, RoleValidationService roleService) {
        this.accountService = accountService;
        this.personService = personService;
        this.identityService = identityService;
        this.roleService = roleService;
    }

    void validateRequest(AccountRequestDto accountDto) {
        List<FieldInfoError> fields = new ArrayList<>();
        personService.validatePersonRegistered(accountDto.getPersonId()).ifPresent(fields::add);
        //Null id as is account creation
        accountService.validatePersonAssigned(null, accountDto).ifPresent(fields::add);
        //Null id as is account creation
        accountService.validateUniqueEmail(null, accountDto).ifPresent(fields::add);

        if(accountDto.isAdmin()){
            identityService.validateAdminRequired(accountDto, "admin").ifPresent(fields::add);
        }

        if(!fields.isEmpty()) throw new ValidationServiceException(fields);
    }

    void validateUpdateRequest(Long accountPathId, AccountUpdateRequestDto accountDto, Long personId) {
        List<FieldInfoError> fields = new ArrayList<>();

        identityService.validateUserAllowance(personId).ifPresent(fields::add);

        if(accountDto.getPersonId() != null){ 
            personService.validatePersonRegistered(accountDto.getPersonId()).ifPresent(fields::add);
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

    void validateByIdRequest(Long personId) {
        identityService.validateUserAllowance(personId).ifPresent(f -> {
            throw new ValidationServiceException(new ArrayList<>(List.of(f)));
        });
    }

}
