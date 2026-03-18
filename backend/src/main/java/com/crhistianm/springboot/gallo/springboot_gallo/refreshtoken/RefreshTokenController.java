package com.crhistianm.springboot.gallo.springboot_gallo.refreshtoken;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth/refresh")
class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping
    ResponseEntity<Map<String, String>> generateNewToken(@RequestBody RefreshTokenRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(refreshTokenService.refreshAccessToken(requestDto));
    }

}
