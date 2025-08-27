package com.crhistianm.springboot.gallo.springboot_gallo.service;


import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.security.custom.CustomAccountUserDetails;


@Service
public class IdentityVerificationServiceImpl implements IdentityVerificationService {

    private final AccountRepository accountRepository;

    public IdentityVerificationServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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

    
}
