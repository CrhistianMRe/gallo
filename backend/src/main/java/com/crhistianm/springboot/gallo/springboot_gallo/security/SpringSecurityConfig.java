package com.crhistianm.springboot.gallo.springboot_gallo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.crhistianm.springboot.gallo.springboot_gallo.security.filter.JwtAuthenticationFilter;
import com.crhistianm.springboot.gallo.springboot_gallo.security.filter.JwtValidationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    public SpringSecurityConfig(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    } 

    @Bean
    AuthenticationManager authenticationManager() throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
        return http.authorizeHttpRequests((authz) -> authz 
                    .requestMatchers(HttpMethod.POST, "/api/persons/register").hasAnyRole("ADMIN", "USER")
                    .requestMatchers(HttpMethod.POST, "/api/accounts/register").hasAnyRole("ADMIN", "USER")
                    .requestMatchers("/swagger-ui/**").hasRole("ADMIN")
                    .requestMatchers("/v3/**").hasRole("ADMIN")
                    .anyRequest().authenticated())
                    .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                    .addFilter(new JwtValidationFilter(authenticationManager()))
                .csrf(config -> config.disable())
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();

    }
    
}

