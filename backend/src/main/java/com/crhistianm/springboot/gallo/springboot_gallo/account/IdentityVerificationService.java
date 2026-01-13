package com.crhistianm.springboot.gallo.springboot_gallo.account;


import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.person.PersonRequestDto;
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

    @Transactional(readOnly = true)
    boolean isUserPersonEntityAllowed(Long id) {
        boolean result = false;
        Optional<Account> accountOptional = accountRepository.findAccountByPersonId(id);
        if(accountOptional.isEmpty()){
            result = true;
            throw new NotFoundException(Person.class);
        }
        String emailDb = accountOptional.orElseThrow().getEmail();
        CustomAccountUserDetails customAccount = (CustomAccountUserDetails)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if(emailDb.equals(customAccount.getEmail())) result = true;
        return result;
    }

    public Optional<FieldInfoError> validateUserAllowance(Long pathPersonId) {
        FieldInfoError infoError = null;
        if(pathPersonId != null && (!isAdminAuthority() && !isUserPersonEntityAllowed(pathPersonId))){
            infoError = new FieldInfoErrorBuilder()
                    .name("path id")
                    .value(pathPersonId)
                    .type(pathPersonId.getClass())
                    .ownerClass(PersonRequestDto.class)
                    .errorMessage(env.getProperty("identity.validation.UserAllowance"))
                    .build();
        }
        return Optional.ofNullable(infoError);
    }

    @Transactional(readOnly = true)
    public Optional<FieldInfoError> validateAllowanceByAccountId(Long accountId) {
        Long personId = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException(Account.class))
            .getPerson().getId();
        return this.validateUserAllowance(personId);
    }

    Optional<FieldInfoError> validateAdminRequired(RequestDto targetDto, String fieldName){
        FieldInfoError infoError = null;
        if(!isAdminAuthority()){
            infoError = FieldInfoErrorMapper.classTargetToFieldInfo(targetDto, fieldName, env.getProperty("identity.validation.AdminRequired"));
        } 
        return Optional.ofNullable(infoError);
    }

    
}
