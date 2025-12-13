package com.crhistianm.springboot.gallo.springboot_gallo.validation.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.service.IdentityVerificationService;

@Component
public class WorkoutValidator {

    private final IdentityVerificationService identityService;

    private final AccountRepository accountRepository;

    public WorkoutValidator(IdentityVerificationService identityService, AccountRepository accountRepository) {
        this.identityService = identityService;
        this.accountRepository = accountRepository;
    }

    public void validateByIdRequest(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException(Account.class));
        identityService.validateUserAllowance(account.getPerson().getId()).ifPresent(f -> {
            throw new ValidationServiceException(List.of(f));
        });
    }
    
}
