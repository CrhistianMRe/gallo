package com.crhistianm.springboot.gallo.springboot_gallo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.PersonBuilder;
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.*;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountAdminResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountCreateDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountUserResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.PersonMapper;
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
        when(roleRepository.findByName("ROLE_USER")).thenReturn(createUserRole());
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(createAdminRole());
        when(personRepository.findById(anyLong())).thenReturn(Optional.of(new PersonBuilder().firstName("one").lastName("1one").id(1L).build()));
        when(accountRepository.save(any())).thenAnswer(arg -> arg.getArgument(0));
    }

    @DisplayName("Testing role user assignment")
	@Test
	void testAssignRoleUser() {
        AccountCreateDto accountUserDto = createAccountDto().orElseThrow();

        //One role
        assertTrue(accountService.save(accountUserDto) instanceof AccountUserResponseDto);
        verify(roleRepository, times(1)).findByName(anyString());
	}

    @DisplayName("Testing role user and admin assignment")
	@Test
	void testAssignRoleAdmin() {
        AccountCreateDto accountAdminDto = createAccountAdminDto().orElseThrow();

        AccountResponseDto accountResponseDto = accountService.save(accountAdminDto);

        AccountAdminResponseDto accountAdminResponseDto = (AccountAdminResponseDto) accountResponseDto;

        //Both roles
        assertTrue(accountResponseDto instanceof AccountAdminResponseDto);
        assertEquals(Arrays.asList(createUserRole().orElseThrow(), createAdminRole().orElseThrow()), accountAdminResponseDto.getRoles());
        verify(roleRepository, times(2)).findByName(anyString());
	}

    @DisplayName("Testing service person assignment")
    @Test
    void testAssignPerson(){
        AccountCreateDto accountCreateDto = createAccountAdminDto().orElseThrow();
        

        AccountAdminResponseDto accountAdminResponseDto = (AccountAdminResponseDto) accountService.save(accountCreateDto);
        Person answer = PersonMapper.createToEntity(createPersonOneDto().orElseThrow());
        answer.setId(1L);
        //Per person test
        assertNotNull(accountAdminResponseDto.getPerson());
        verify(personRepository, times(1)).findById(anyLong());
        assertEquals(answer, (accountAdminResponseDto.getPerson()));
    }

}
