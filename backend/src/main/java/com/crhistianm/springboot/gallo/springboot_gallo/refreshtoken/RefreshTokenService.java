package com.crhistianm.springboot.gallo.springboot_gallo.refreshtoken;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.account.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityManager;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final EntityManager entityManager;

    RefreshTokenService
        (
         RefreshTokenRepository refreshTokenRepository,
         EntityManager entityManager
         ) {
            this.refreshTokenRepository = refreshTokenRepository;
            this.entityManager = entityManager;
        }

    @Transactional(readOnly = true)
    Map<String, String> refreshAccessToken(final RefreshTokenRequestDto requestDto) {

        final String refreshToken = requestDto.getRefreshToken();

        Account account = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new NotFoundException(RefreshToken.class))
            .getAccount(); 

        Collection<? extends GrantedAuthority> authorities = account.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

        String email = account.getEmail();

        //One hour in millis
        final Date expiresAt = new Date(System.currentTimeMillis() + 3600000);

        Claims claims = null;

        try {
            claims = Jwts.claims()
                .add("authorities", new ObjectMapper().writeValueAsString(authorities))
                .add("username", email)
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize authorities", e);
        }

        final String accessToken = JwtUtils.createAccessJwt(email, claims, expiresAt);

        Map<String, String> response = new HashMap<>();

        response.put("accessToken", accessToken);
        response.put("expiresAt", expiresAt.toInstant().toString());

        return response;
    }

    @Transactional
    public String createRefreshToken(final Long accountId) {
        final String refreshToken = UUID.randomUUID().toString();

        LocalDateTime expiresAt = LocalDateTime.now().plusMonths(1);

        RefreshToken refreshTokenEntity = new RefreshTokenBuilder()
            .token(refreshToken)
            .revoked(false)
            .expiresAt(expiresAt)
            .account(entityManager.getReference(Account.class, accountId))
            .build();

        refreshTokenRepository.save(refreshTokenEntity);

        return refreshToken;
    }

}
