package com.crhistianm.springboot.gallo.springboot_gallo.refreshtoken;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.NotFoundException;


@Service
class RefreshTokenValidationService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final Environment env;

    RefreshTokenValidationService(RefreshTokenRepository refreshTokenRepository, Environment env) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.env = env;
    }

    @Transactional(readOnly = true)
    boolean isRefreshTokenExpired(String refreshToken) {
        RefreshToken dbToken = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new NotFoundException(RefreshToken.class));

        LocalDateTime expiresAtDate =  dbToken.getExpiresAt();

        return expiresAtDate.isBefore(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    boolean isRefreshTokenRevoked(String refreshToken) {
        RefreshToken dbToken = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new NotFoundException(RefreshToken.class));

        return dbToken.getRevoked();
    }

    Optional<FieldInfoError> validateRefreshTokenExpirationDate(String refreshToken) {
        FieldInfoError error = null;

        if(isRefreshTokenExpired(refreshToken)) {
            error = new FieldInfoErrorBuilder()
                .name("refreshToken")
                .type(refreshToken.getClass())
                .value(refreshToken)
                .errorMessage(env.getProperty("refreshtoken.validation.RefreshTokenExpired"))
                .build();
        }

        return Optional.ofNullable(error);
    }
 
    Optional<FieldInfoError> validateRefreshTokenRevocation(String refreshToken) {
        FieldInfoError error = null;

        if(isRefreshTokenRevoked(refreshToken)) {

            error = new FieldInfoErrorBuilder()
                .name("refreshToken")
                .type(refreshToken.getClass())
                .value(refreshToken)
                .errorMessage(env.getProperty("refreshtoken.validation.RefreshTokenRevoked"))
                .build();
        }

        return Optional.ofNullable(error);
    }
    
    
}
