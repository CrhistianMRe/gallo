package com.crhistianm.springboot.gallo.springboot_gallo.validation.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.service.IdentityVerificationService;

@Component
public class WorkoutValidator {

    private final IdentityVerificationService identityService;

    public WorkoutValidator(IdentityVerificationService identityService) {
        this.identityService = identityService;
    }

    public void validateByIdRequest(Long accountId) {
        identityService.validateAllowanceByAccountId(accountId).ifPresent(f -> {
            throw new ValidationServiceException(List.of(f));
        });
    }
    
}
