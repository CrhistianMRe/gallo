package com.crhistianm.springboot.gallo.springboot_gallo.controller;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.AccountMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.PersonMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.security.SpringSecurityConfig;
import com.crhistianm.springboot.gallo.springboot_gallo.service.AccountService;
import com.crhistianm.springboot.gallo.springboot_gallo.service.AccountUserDetailsService;
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

    @MockitoBean
    AccountUserDetailsService service;

    
    @Test
    void testCreate() throws Exception{
        AccountRequestDto accountDto = givenAdminAccountRequestDto().orElseThrow(); 

        Person person = new PersonBuilder().id(1L).build();
        AccountAdminResponseDto accountResponseDto = new AccountAdminResponseDto();
        accountResponseDto.setEmail("erikadmin@gmail.com");
        accountResponseDto.setPerson(PersonMapper.entityToResponse(person));
            

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

    @Nested
    class ViewModuleTest {

        @BeforeEach
        void setUp(){
            when(accountService.getById(anyLong())).thenAnswer(invo ->{
                if(invo.getArgument(0, Long.class) == 99L) throw new NotFoundException(Account.class);
                if(invo.getArgument(0, Long.class) == 1L) return AccountMapper.entityToAdminResponse(givenAccountEntityAdmin().orElseThrow());
                return AccountMapper.entityToResponse(givenAccountEntityUser().orElseThrow());
            });
        }

        @Test
        void testViewByIdAdmin() throws Exception {
            mockMvc.perform(get("/api/accounts/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("admin@gmail.com"));
        }

        @Test
        void testViewByIdUser() throws Exception {
            mockMvc.perform(get("/api/accounts/2"))
                .andExpect(jsonPath("$.email").value("user@gmail.com"));
        }

        @Test
        void testViewByIdNotFound() throws Exception{
            mockMvc.perform(get("/api/accounts/99"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Account not found"))
                .andExpect(status().isNotFound());
        }



    }
    
}
