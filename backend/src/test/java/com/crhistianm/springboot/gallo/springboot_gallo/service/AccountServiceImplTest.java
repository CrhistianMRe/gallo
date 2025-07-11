package com.crhistianm.springboot.gallo.springboot_gallo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.AccountDtoBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.data.Data;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountDto;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.PersonRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.RoleRepository;

@SpringBootTest
class AccountServiceImplTest {

    @MockitoBean
    AccountRepository accountRepository;

    @MockitoBean
    RoleRepository roleRepository;

    @MockitoBean
    PersonRepository personRepository;

    @Autowired
    AccountService accountService;

    @BeforeEach
    void setUp(){
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Data.createUserRole());
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Data.createAdminRole());
        when(personRepository.findById(anyLong())).thenReturn(Data.createPerson());
        when(accountRepository.save(any())).thenAnswer(arg -> arg.getArgument(0));
    }

    @DisplayName("Testing role user assignment")
	@Test
	void testAssignRoleUser() {
        AccountDto accountUser = new AccountDtoBuilder().personId(1L).password("12345").admin(false).build();

        //One role
        assertEquals(Arrays.asList(Data.createUserRole().orElseThrow()), accountService.save(accountUser).getRoles());
        verify(roleRepository, times(1)).findByName(anyString());
	}

    @DisplayName("Testing role user and admin assignment")
	@Test
	void testAssignRoleAdmin() {
        AccountDto accountAdmin = new AccountDtoBuilder().personId(2L).password("12345").admin(true).build();

        //Both roles
        assertEquals(Arrays.asList(Data.createUserRole().orElseThrow(), Data.createAdminRole().orElseThrow()), accountService.save(accountAdmin).getRoles());
        verify(roleRepository, times(2)).findByName(anyString());
	}

    @DisplayName("Testing service person assignment")
    @Test
    void testAssignPerson(){
        AccountDto accountUser = new AccountDtoBuilder().password("12345").personId(1L).build();

        //Per person test
        assertNotNull(accountService.save(accountUser).getPerson());
        verify(personRepository, times(1)).findById(anyLong());
        assertEquals(Data.createPerson().orElseThrow(), (accountService.save(accountUser).getPerson()));
    }

}
