package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import java.util.List;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.account.IdentityVerificationService;

@Component
class WorkoutValidator {

    private final IdentityVerificationService identityService;

    WorkoutValidator(IdentityVerificationService identityService) {
        this.identityService = identityService;
    }

    void validateByIdRequest(Long accountId) {
        identityService.validateAllowanceByAccountId(accountId).ifPresent(f -> {
            throw new ValidationServiceException(List.of(f));
        });
    }
    
}
