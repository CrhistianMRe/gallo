package com.crhistianm.springboot.gallo.springboot_gallo.shared.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.crhistianm.springboot.gallo.springboot_gallo.account.AccountUserDetailsService;
import com.crhistianm.springboot.gallo.springboot_gallo.refreshtoken.RefreshTokenService;

@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthenticationFilterTest {

    private static final String LOGIN_ENDPOINT = "/api/auth/login";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private AccountUserDetailsService userDetailsService;

    private String createJsonRequest(String email, String password) {
        String request = String.format("""
        {
            "email": "%s",
            "password": "12345"
        }
        """, email);
        return request;
    }


    @Nested
    class LoginTest {

        String email;

        String password;

        @BeforeEach
        void setUp() {
            doReturn("refreshToken").when(refreshTokenService).createRefreshToken(anyLong());

            when(userDetailsService.loadUserByUsername(anyString())).thenAnswer(invo ->{
                final String argEmail = invo.getArgument(0, String.class);

                List<GrantedAuthority> authorities = new ArrayList<>();

                CustomAccountUserDetails userDetails = null;


                if(argEmail.contains("correct")){
                    userDetails = new CustomAccountUserDetails(
                            1L,
                            passwordEncoder.encode("12345"),
                            invo.getArgument(0),
                            invo.getArgument(0),
                            true,
                            true,
                            true,
                            true,
                            authorities
                            );
                } else { throw new UsernameNotFoundException(""); }

                return userDetails;
            });
        }

        @Test
        void shouldReturnValidLoginResponse() throws Exception {

            String email = "correct@gmail.com";

            mockMvc.perform(post(LOGIN_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createJsonRequest(email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(Matchers.startsWith("eyJ")))
                .andExpect(jsonPath("$.expiresAt").value(Matchers.notNullValue()))
                .andExpect(jsonPath("$.accountId").value(Matchers.equalTo(1)))
                .andExpect(jsonPath("$.refreshToken").value(Matchers.equalTo("refreshToken")))
                .andExpect(jsonPath("$.message").value(Matchers.equalTo("You have successfully logged in")));
        }

        @Test
        void shouldReturnInvalidLoginResponse() throws Exception {
            String email = "wrong@gmail.com";

            mockMvc.perform(post(LOGIN_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createJsonRequest(email, password)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(Matchers.equalTo("email or password not valid, authentication failed")));

        }

    }

    
}
