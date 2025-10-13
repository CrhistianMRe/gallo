package com.crhistianm.springboot.gallo.springboot_gallo.security.filter;

import static com.crhistianm.springboot.gallo.springboot_gallo.security.TokenJwtConfig.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.crhistianm.springboot.gallo.springboot_gallo.security.SimpleGrantedAuthorityJsonCreator;
import com.crhistianm.springboot.gallo.springboot_gallo.security.custom.CustomAccountUserDetails;
import com.crhistianm.springboot.gallo.springboot_gallo.service.AccountUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtValidationFilter extends BasicAuthenticationFilter {

    private final AccountUserDetailsService accountService;

    private final Environment env;

    public JwtValidationFilter(AuthenticationManager authenticationManager, AccountUserDetailsService accountService, Environment env) {
        super(authenticationManager);
        this.accountService = accountService;
        this.env= env;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

            String header = request.getHeader(HEADER_AUTHORIZATION);

            if(header == null || !header.startsWith(PREFIX_TOKEN)){
                chain.doFilter(request, response);
                return;
            }
            String token = header.replace(PREFIX_TOKEN, "");

            try {

                Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();

                String email = claims.getSubject();

                CustomAccountUserDetails customAccountUserDetails = (CustomAccountUserDetails) accountService.loadUserByUsername(email);

                Object authoritiesClaims = claims.get("authorities");

                Collection<? extends GrantedAuthority> authorities = Arrays.asList(
                        new ObjectMapper()
                        //Use JsonCreator to use role instead of authority
                        .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
                        //Read array of SimpleGrantedAuthority values
                        .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class)
                        );

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(customAccountUserDetails, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                chain.doFilter(request, response);
                
            } catch (Exception e) {
                Map<String, String> body = new HashMap<String, String>();

                body.put("error", e.getMessage());
                body.put("message", env.getProperty("filter.validation.token"));

                response.getWriter().write(new ObjectMapper().writeValueAsString(body));
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(CONTENT_TYPE);

            }
    }
     

    
}
