package com.crhistianm.springboot.gallo.springboot_gallo.refreshtoken;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;

@Component
class RefreshTokenValidator {

    private final RefreshTokenValidationService tokenValidationService;

    RefreshTokenValidator(RefreshTokenValidationService tokenValidationService) {
        this.tokenValidationService = tokenValidationService;
    }

    void validateTokenRefresh(final String refreshToken) {
        List<FieldInfoError> errors = new ArrayList<>();

        tokenValidationService.validateRefreshTokenExpirationDate(refreshToken).ifPresent(errors::add);
        tokenValidationService.validateRefreshTokenRevocation(refreshToken).ifPresent(errors::add);

        if(!errors.isEmpty()) throw new ValidationServiceException(errors);
    }

    
}
