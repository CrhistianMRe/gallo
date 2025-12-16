package com.crhistianm.springboot.gallo.springboot_gallo.service;


import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.FieldInfoErrorBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AbstractAccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.FieldInfoErrorMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.security.custom.CustomAccountUserDetails;


@Service
public class IdentityVerificationServiceImpl implements IdentityVerificationService {

    private final AccountRepository accountRepository;

    private final Environment env;

    public IdentityVerificationServiceImpl(AccountRepository accountRepository, Environment env) {
        this.accountRepository = accountRepository;
        this.env = env;
    }

    @Override
    public boolean isAdminAuthority(){
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isUserPersonEntityAllowed(Long id) {
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

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public Optional<FieldInfoError> validateAllowanceByAccountId(Long accountId) {
        Long personId = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException(Account.class))
            .getPerson().getId();
        return this.validateUserAllowance(personId);
    }

    @Override
    public Optional<FieldInfoError> validateAdminRequired(AbstractAccountRequestDto accountDto, String fieldName){
        FieldInfoError infoError = null;
        if(!isAdminAuthority()){
            infoError = FieldInfoErrorMapper.classTargetToFieldInfo(accountDto, fieldName, env.getProperty("identity.validation.AdminRequired"));
        } 
        return Optional.ofNullable(infoError);
    }

    
}
