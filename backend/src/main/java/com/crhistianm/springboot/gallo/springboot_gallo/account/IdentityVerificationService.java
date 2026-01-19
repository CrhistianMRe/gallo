package com.crhistianm.springboot.gallo.springboot_gallo.account;


import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.RequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.person.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.security.CustomAccountUserDetails;


@Service
public class IdentityVerificationService {

    private final AccountRepository accountRepository;

    private final Environment env;

    public IdentityVerificationService(AccountRepository accountRepository, Environment env) {
        this.accountRepository = accountRepository;
        this.env = env;
    }

    boolean isAdminAuthority(){
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }

    boolean isUserAllowed(Long accountId) {
        CustomAccountUserDetails customAccount = (CustomAccountUserDetails)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return isAdminAuthority() || customAccount.getId().equals(accountId);
    }

    @Transactional(readOnly = true)
    public Optional<FieldInfoError> validateUserAllowanceByPersonId(Long personId) {
        FieldInfoError infoError = null;
        Long accountId = accountRepository.findAccountByPersonId(personId).orElseThrow(() -> new NotFoundException(Account.class)).getId();
        if(!isUserAllowed(accountId)){
            infoError = new FieldInfoErrorBuilder()
                    .name("path id")
                    .value(personId)
                    .type(personId.getClass())
                    .errorMessage(env.getProperty("identity.validation.UserAllowance"))
                    .build();
        }
        return Optional.ofNullable(infoError);
    }

    @Transactional(readOnly = true)
    public Optional<FieldInfoError> validateAllowanceByAccountId(Long accountId) {
        Long personId = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException(Account.class))
            .getPerson().getId();
        return this.validateUserAllowanceByPersonId(personId);
    }

    Optional<FieldInfoError> validateAdminRequired(RequestDto targetDto, String fieldName){
        FieldInfoError infoError = null;
        if(!isAdminAuthority()){
            infoError = FieldInfoErrorMapper.classTargetToFieldInfo(targetDto, fieldName, env.getProperty("identity.validation.AdminRequired"));
        } 
        return Optional.ofNullable(infoError);
    }

    
}
