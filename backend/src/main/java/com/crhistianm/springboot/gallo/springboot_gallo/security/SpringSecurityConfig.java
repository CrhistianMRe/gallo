package com.crhistianm.springboot.gallo.springboot_gallo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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
import com.crhistianm.springboot.gallo.springboot_gallo.service.AccountUserDetailsService;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {

    private final AccountUserDetailsService accountService;

    private final AuthenticationConfiguration authenticationConfiguration;

    private final Environment environment;

    public SpringSecurityConfig(AuthenticationConfiguration authenticationConfiguration, AccountUserDetailsService accountService, Environment environment) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.accountService = accountService;
        this.environment = environment;
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
                .requestMatchers(HttpMethod.GET, "/api/persons").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/persons/{id}").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.PUT, "/api/persons/{id}").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.DELETE, "/api/persons/{id}").hasAnyRole("ADMIN", "USER")
                    .requestMatchers(HttpMethod.POST, "/api/persons/register").hasAnyRole("ADMIN", "USER")
                    .requestMatchers(HttpMethod.POST, "/api/accounts/register").hasAnyRole("ADMIN", "USER")
                    .requestMatchers(HttpMethod.GET, "/api/accounts/{id}").hasAnyRole("ADMIN", "USER")
                    .requestMatchers(HttpMethod.PUT, "/api/accounts/{id}").hasAnyRole("ADMIN", "USER")
                    .requestMatchers(HttpMethod.DELETE, "/api/accounts/{id}").hasAnyRole("ADMIN", "USER")
                    .requestMatchers(HttpMethod.GET, "/api/accounts").hasAnyRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/body-parts").hasAnyRole("ADMIN", "USER")
                    .requestMatchers("/swagger-ui/**").hasRole("ADMIN")
                    .requestMatchers("/v3/**").hasRole("ADMIN")
                    .anyRequest().authenticated())
                    .addFilter(new JwtAuthenticationFilter(authenticationManager(), environment))
                    .addFilter(new JwtValidationFilter(authenticationManager(), accountService, environment))
                .csrf(config -> config.disable())
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();

    }
    
}

