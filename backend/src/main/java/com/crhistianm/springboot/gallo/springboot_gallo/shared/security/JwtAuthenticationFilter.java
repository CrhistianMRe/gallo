package com.crhistianm.springboot.gallo.springboot_gallo.shared.security;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.crhistianm.springboot.gallo.springboot_gallo.account.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.refreshtoken.RefreshTokenService;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.crhistianm.springboot.gallo.springboot_gallo.shared.security.TokenJwtConfig.*;

class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final Environment env;

    private final RefreshTokenService refreshTokenService;

    private final ObjectMapper objectMapper;

    JwtAuthenticationFilter
        (
         AuthenticationManager authenticationManager,
         Environment env,
         RefreshTokenService refreshTokenService,
         ObjectMapper objectMapper 
        ) {
            setFilterProcessesUrl("/api/auth/login");
            this.authenticationManager = authenticationManager;
            this.env = env;
            this.refreshTokenService = refreshTokenService;
            this.objectMapper = objectMapper;
        }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

            Account account = null;
            String email = null;
            String password = null;

            try {
                account = new ObjectMapper().readValue(request.getInputStream(), Account.class);
                password = account.getPassword();
                email = account.getEmail();
            } catch (StreamReadException e) {
                e.printStackTrace();
            } catch (DatabindException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
            return this.authenticationManager.authenticate(authenticationToken);

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        CustomAccountUserDetails accountUserDetails = (CustomAccountUserDetails) authResult.getPrincipal();
        String email = accountUserDetails.getEmail();
        Long accountId = accountUserDetails.getId();

        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();

        Claims claims = Jwts.claims()
            .add("authorities", new ObjectMapper().writeValueAsString(roles))
            .add("accountId", accountId)
            .add("username", email)
            .build();

        //One hour in millis
        final Date expiresAt = new Date(System.currentTimeMillis() + 3600000);

        String accessToken = JwtUtils.createAccessJwt(email, claims, expiresAt);

        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + accessToken);

        String refreshToken = refreshTokenService.createRefreshToken(accountId);

        SuccesfulAuthResponseDto responseDto = SuccesfulAuthResponseDto.builder()
            .accessToken(accessToken)
            .expiresAt(expiresAt.toInstant().toString())
            .accountId(accountId)
            .refreshToken(refreshToken)
            .message(env.getProperty("filter.authentication.successful"))
            .build();

        response.getWriter().write(objectMapper.writeValueAsString(responseDto));
        response.setContentType(CONTENT_TYPE);
        response.setStatus(200);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {

        Map<String, String> body = new HashMap<String, String>();

        body.put("message", env.getProperty("filter.authentication.unsuccessful"));
        body.put("error", failed.getMessage());

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);
        response.setContentType(CONTENT_TYPE);

    }



    
}
