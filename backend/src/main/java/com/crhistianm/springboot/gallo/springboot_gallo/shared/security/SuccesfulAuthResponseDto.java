package com.crhistianm.springboot.gallo.springboot_gallo.shared.security;

final class SuccesfulAuthResponseDto {

    private final String accessToken;

    private final String expiresAt;

    private final Long accountId;

    private final String refreshToken;

    private final String message;

    private SuccesfulAuthResponseDto
        (
         String accessToken,
         String expiresAt,
         Long accountId,
         String refreshToken,
         String message
        ) {
            this.accessToken = accessToken;
            this.expiresAt = expiresAt;
            this.accountId = accountId;
            this.refreshToken = refreshToken;
            this.message = message;
        }

    String getAccessToken() {
        return accessToken;
    }

    String getExpiresAt() {
        return expiresAt;
    }

    Long getAccountId() {
        return accountId;
    }

    String getRefreshToken() {
        return refreshToken;
    }

    String getResponseMessage() {
        return message;
    }

    static Builder builder(){ return new Builder(); }

    static class Builder {

        private String accessToken;

        private String expiresAt;

        private Long accountId;

        private String refreshToken;

        private String message;

        Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        Builder expiresAt(String expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        Builder accountId(Long accountId) {
            this.accountId = accountId;
            return this;
        }

        Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        Builder message(String message) {
            this.message = message;
            return this;
        }

        SuccesfulAuthResponseDto build() {
            return new SuccesfulAuthResponseDto
                (
                 this.accessToken,
                 this.expiresAt,
                 this.accountId,
                 this.refreshToken,
                 this.message 
                );
        }

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accessToken == null) ? 0 : accessToken.hashCode());
        result = prime * result + ((expiresAt == null) ? 0 : expiresAt.hashCode());
        result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SuccesfulAuthResponseDto other = (SuccesfulAuthResponseDto) obj;
        if (accessToken == null) {
            if (other.accessToken != null)
                return false;
        } else if (!accessToken.equals(other.accessToken))
            return false;
        if (expiresAt == null) {
            if (other.expiresAt != null)
                return false;
        } else if (!expiresAt.equals(other.expiresAt))
            return false;
        if (accountId == null) {
            if (other.accountId != null)
                return false;
        } else if (!accountId.equals(other.accountId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SuccesfulAuthResponseDto [accessToken=" + accessToken + ", expiresAt=" + expiresAt + ", accountId="
                + accountId + ", refreshToken=" + refreshToken + ", message=" + message + "]";
    }


}
