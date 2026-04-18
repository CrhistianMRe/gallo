package com.crhistianm.springboot.gallo.springboot_gallo.account;

import java.util.List;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.ValidationServiceErrorList;
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
        ValidationServiceErrorList errorList = new ValidationServiceErrorList();

        personService.validatePersonRegistered(accountDto.getPersonId()).ifPresent(errorList::add);
        //Null id as is account creation
        accountService.validatePersonAssigned(null, accountDto).ifPresent(errorList::add);
        //Null id as is account creation
        accountService.validateUniqueEmail(null, accountDto).ifPresent(errorList::add);

        if(accountDto.isAdmin()){
            identityService.validateAdminRequired(accountDto, "admin").ifPresent(errorList::add);
        }

        errorList.throwFieldErrors();
    }

    void validateUpdateRequest(Long accountPathId, AccountUpdateRequestDto accountDto) {
        ValidationServiceErrorList errorList = new ValidationServiceErrorList();

        identityService.validateAllowanceByAccountId(accountPathId).ifPresent(errorList::add);

        if(accountDto.getPersonId() != null){ 
            personService.validatePersonRegistered(accountDto.getPersonId()).ifPresent(errorList::add);
            accountService.validatePersonAssigned(accountPathId, accountDto).ifPresent(errorList::add);
            identityService.validateAdminRequired(accountDto, "personId").ifPresent(errorList::add);
        }
        if(accountDto.getEmail() != null) accountService.validateUniqueEmail(accountPathId, accountDto).ifPresent(errorList::add);
        if(accountDto.isEnabled() != null) identityService.validateAdminRequired(accountDto, "enabled").ifPresent(errorList::add);
        if(!accountDto.getRoles().isEmpty()) {
            identityService.validateAdminRequired(accountDto, "roles").ifPresent(errorList::add);
            errorList.addAll(accountDto.getRoles().stream().flatMap(role -> roleService.validateRoleExists(role).stream()).toList());
        }

        errorList.throwFieldErrors();
    }

    void validateByIdRequest(Long accountId) {
        identityService.validateAllowanceByAccountId(accountId).ifPresent(f -> {
            throw new ValidationServiceException(List.of(f));
        });
    }

}
