package com.crhistianm.springboot.gallo.springboot_gallo.refreshtoken;

class RefreshTokenResponseDto {

    private String accessToken;

    private String expiresAt;

    RefreshTokenResponseDto(){}

    RefreshTokenResponseDto(String accessToken, String expiresAt) {
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
    }

    String getAccessToken() {
        return accessToken;
    }

    void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    String getExpiresAt() {
        return expiresAt;
    }

    void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accessToken == null) ? 0 : accessToken.hashCode());
        result = prime * result + ((expiresAt == null) ? 0 : expiresAt.hashCode());
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
        RefreshTokenResponseDto other = (RefreshTokenResponseDto) obj;
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
        return true;
    }

    @Override
    public String toString() {
        return "RefreshTokenResponseDto [accessToken=" + accessToken + ", expiresAt=" + expiresAt + "]";
    }
    
}
