package com.crhistianm.springboot.gallo.springboot_gallo.refreshtoken;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final class RefreshTokenRequestDto {

    private final String refreshToken;

    @JsonCreator
    RefreshTokenRequestDto(@JsonProperty("refreshToken") String refreshToken) {
        this.refreshToken = refreshToken;
    }

    String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((refreshToken== null) ? 0 : refreshToken.hashCode());
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
        RefreshTokenRequestDto other = (RefreshTokenRequestDto) obj;
        if (refreshToken == null) {
            if (other.refreshToken != null)
                return false;
        } else if (!refreshToken.equals(other.refreshToken))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RefreshTokenRequestDto [refreshToken=" + refreshToken + "]";
    }
}
