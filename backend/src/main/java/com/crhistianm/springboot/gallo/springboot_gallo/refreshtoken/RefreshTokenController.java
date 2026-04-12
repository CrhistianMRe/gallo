package com.crhistianm.springboot.gallo.springboot_gallo.refreshtoken;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/auth/refresh")
class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @SecurityRequirements(value = {})
    @PostMapping
    ResponseEntity<RefreshTokenResponseDto> generateNewToken(@Valid @RequestBody RefreshTokenRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(refreshTokenService.refreshAccessToken(requestDto));
    }

}
