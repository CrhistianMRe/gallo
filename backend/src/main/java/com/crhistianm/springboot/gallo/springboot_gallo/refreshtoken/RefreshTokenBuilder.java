package com.crhistianm.springboot.gallo.springboot_gallo.refreshtoken;

import java.time.LocalDateTime;

import com.crhistianm.springboot.gallo.springboot_gallo.account.Account;

class RefreshTokenBuilder {

    private Long id;

    private String token;

    private LocalDateTime expiresAt;

    private Boolean revoked;

    private Account account;

    RefreshTokenBuilder id(Long id) {
        this.id = id;
        return this;
    }

    RefreshTokenBuilder token(String token) {
        this.token = token;
        return this;
    }

    RefreshTokenBuilder expiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    RefreshTokenBuilder revoked(Boolean revoked) {
        this.revoked = revoked;
        return this;
    }

    RefreshTokenBuilder account(Account account) {
        this.account = account;
        return this;
    }

    RefreshToken build() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(this.id);
        refreshToken.setToken(this.token);
        refreshToken.setRevoked(this.revoked);
        refreshToken.setAccount(this.account);
        refreshToken.setExpiresAt(this.expiresAt);
        return refreshToken;
    }

}
