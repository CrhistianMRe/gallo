package com.crhistianm.springboot.gallo.springboot_gallo.shared.security;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;


class TokenJwtConfig {

    static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    static final String PREFIX_TOKEN = "Bearer ";
    static final String HEADER_AUTHORIZATION = "Authorization";
    static final String CONTENT_TYPE = "application/json";

}
