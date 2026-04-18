package com.crhistianm.springboot.gallo.springboot_gallo.refreshtoken;

import org.springframework.stereotype.Component;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.ValidationServiceErrorList;

@Component
class RefreshTokenValidator {

    private final RefreshTokenValidationService tokenValidationService;

    RefreshTokenValidator(RefreshTokenValidationService tokenValidationService) {
        this.tokenValidationService = tokenValidationService;
    }

    void validateTokenRefresh(final String refreshToken) {
        ValidationServiceErrorList errors = new ValidationServiceErrorList();

        tokenValidationService.validateRefreshTokenExpirationDate(refreshToken).ifPresent(errors::add);
        tokenValidationService.validateRefreshTokenRevocation(refreshToken).ifPresent(errors::add);

        errors.throwFieldErrors();
    }

    
}
