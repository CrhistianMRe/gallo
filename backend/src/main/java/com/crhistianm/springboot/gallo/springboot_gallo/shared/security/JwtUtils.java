package com.crhistianm.springboot.gallo.springboot_gallo.shared.security;

import static com.crhistianm.springboot.gallo.springboot_gallo.shared.security.TokenJwtConfig.SECRET_KEY;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtUtils{

    public static String createAccessJwt(final String email, final Claims claims, final Date expiresAt) {

        String token = Jwts.builder()
            .subject(email)
            .claims(claims)
            .expiration(expiresAt)
            .issuedAt(new Date())
            .signWith(SECRET_KEY).compact();

        return token;
    }
    
}
