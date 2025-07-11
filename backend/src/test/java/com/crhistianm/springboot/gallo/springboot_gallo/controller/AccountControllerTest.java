package com.crhistianm.springboot.gallo.springboot_gallo.controller;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.*;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.AccountBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.security.SpringSecurityConfig;
import com.crhistianm.springboot.gallo.springboot_gallo.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AccountController.class)
@Import(SpringSecurityConfig.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService; 

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreate() throws Exception{
        AccountDto accountDto = createAccountAdminDto().orElseThrow(); 

        Account account = new AccountBuilder().email(accountDto.getEmail()).person(createPerson().orElseThrow()).password(accountDto.getPassword()).build();

        //It is a different instance as objectMapper changes it obviously
        when(accountService.save(accountDto)).thenReturn(account);
        System.out.println(objectMapper.writeValueAsString(account));

        //Given
        mockMvc.perform(post("/api/accounts/register")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(accountDto)))

                 //Then
            .andExpect(status().isCreated())
                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                 .andExpect(jsonPath("$.email").value("erikadmin@gmail.com"))
                 .andExpect(jsonPath("$.person.id").value(1L));
    }
    
}
