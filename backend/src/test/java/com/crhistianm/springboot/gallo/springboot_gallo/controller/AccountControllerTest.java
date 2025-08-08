package com.crhistianm.springboot.gallo.springboot_gallo.controller;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.*;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.PersonBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.config.JacksonConfig;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountAdminResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountCreateDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.security.SpringSecurityConfig;
import com.crhistianm.springboot.gallo.springboot_gallo.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Validator;

@WebMvcTest(AccountController.class)
@Import({SpringSecurityConfig.class, JacksonConfig.class})
//remove security filters as is not needed in these tests
@AutoConfigureMockMvc(addFilters = false)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService; 

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    Validator validator;
    
    @Test
    void testCreate() throws Exception{
        AccountCreateDto accountDto = createAccountAdminDto().orElseThrow(); 

        Person person = new PersonBuilder().id(1L).build();
        AccountAdminResponseDto accountResponseDto = new AccountAdminResponseDto();
        accountResponseDto.setEmail("erikadmin@gmail.com");
        accountResponseDto.setPerson(person);
            

        //It is a different instance as objectMapper changes it obviously
        when(accountService.save(accountDto)).thenReturn(accountResponseDto);
        System.out.println(objectMapper.writeValueAsString(accountResponseDto));

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
