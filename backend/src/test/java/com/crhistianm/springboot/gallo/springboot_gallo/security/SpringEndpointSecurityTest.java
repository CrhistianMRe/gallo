package com.crhistianm.springboot.gallo.springboot_gallo.security;

import static com.crhistianm.springboot.gallo.springboot_gallo.security.TokenJwtConfig.CONTENT_TYPE;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.crhistianm.springboot.gallo.springboot_gallo.config.JacksonConfig;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.security.custom.CustomAccountUserDetails;
import com.crhistianm.springboot.gallo.springboot_gallo.service.AccountUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@Import({SpringSecurityConfig.class, JacksonConfig.class})
public class SpringEndpointSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private AccountUserDetailsService service;

    String prefixWithToken;

    String generateToken(String email, String password) throws Exception{

        String request = String.format("""
        {
            "email": "%s",
            "password": "%s"
        }
        """, email, password);

        MvcResult result = mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();



        String token = JsonPath.read(result.getResponse().getContentAsString(), "$.token");

        return token;

    }

    @BeforeEach
    void setUp(){
            when(service.loadUserByUsername(anyString())).thenAnswer(invo ->{
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));


                if(invo.getArgument(0).equals("admin@gmail.com"))authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

                return new CustomAccountUserDetails(
                        passwordEncoder.encode("12345"),
                        invo.getArgument(0),
                        invo.getArgument(0),
                        true,
                        true,
                        true,
                        true,
                        authorities
                        );
                
            });
    }

    @Nested
    class AdminAuthorityEndpointsTest{

        @BeforeEach
        void setUp() throws Exception{
            prefixWithToken = "Bearer ".concat(generateToken("admin@gmail.com", "12345"));
        }

        @Test
        void testSwaggerValidAuthority() throws Exception{

            mockMvc.perform(get("/v3/api-docs")
                    .contentType(MediaType.APPLICATION_JSON).header("Authorization", prefixWithToken))
                .andExpect(status().isOk());

        }

        @Test
        void testUpdateValidAuthority() throws Exception{
            mockMvc.perform(put("/api/person/1")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new PersonRequestDto())).header("Authorization", prefixWithToken))
                .andExpect(status().isNotFound());
        }

    }

    @Nested
    class UserAuthorityEndpointsTest{

        @BeforeEach
        void setUp() throws Exception{
            prefixWithToken = "Bearer ".concat(generateToken("user@gmail.com", "12345"));

        }

        @Test
        void testSwaggerInvalidAuthority()throws Exception{

            mockMvc.perform(get("/v3/api-docs")
                    .contentType(MediaType.APPLICATION_JSON).header("Authorization", prefixWithToken))
                    .andExpect(status().isForbidden());

        }

        @Test
        void testUpdateValidAuthority() throws Exception{
            mockMvc.perform(put("/api/persons/1")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new PersonRequestDto())).header("Authorization", prefixWithToken))
                .andExpect(status().isForbidden());
        }

    }

         

    
}
