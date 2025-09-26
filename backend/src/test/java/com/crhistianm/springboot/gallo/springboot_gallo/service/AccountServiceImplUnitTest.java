package com.crhistianm.springboot.gallo.springboot_gallo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.PersonBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.builder.RoleBuilder;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.*;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.*;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountAdminResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountUserResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.AccountMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.PersonMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.PersonRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.RoleRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.validation.service.AccountValidator;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplUnitTest {

    @Mock
    AccountValidator accountValidator;

    @Mock
    AccountRepository accountRepository;

    @Mock
    AccountValidationService accountValidationService;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PersonRepository personRepository;

    //Not used here just for dependency usage
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    @Nested
    class ViewModuleTest{

        @BeforeEach
        void setUp(){
            lenient().when(accountRepository.findById(anyLong())).thenAnswer(invo -> {
                Optional<Account> accountOptional = Optional.empty();
                if(invo.getArgument(0, Long.class) == 1L) accountOptional = givenAccountEntityAdmin();
                return accountOptional;
            });
        }

        @Test
        void testGetAll(){
            when(accountRepository.findAll()).thenReturn(List.of(givenAccountEntityAdmin().orElseThrow(), givenAccountEntityUser().orElseThrow()));
            List<AccountAdminResponseDto> expectedList = List.of((AccountAdminResponseDto)AccountMapper.entityToAdminResponse(givenAccountEntityAdmin().orElseThrow()), 
                    (AccountAdminResponseDto)AccountMapper.entityToAdminResponse(givenAccountEntityUser().orElseThrow()));
            assertEquals(expectedList, accountServiceImpl.getAll());
            verify(accountRepository, times(1)).findAll();
        }

        @Test
        void testgetByIdNotFound(){
            assertThrows(NotFoundException.class, () -> {
                accountServiceImpl.getById(2L);
            });
            verify(accountRepository, times(1)).findById(anyLong());
        }

        @Test
        void testGetById(){
            when(accountValidationService.settleResponseType(any(Account.class))).thenAnswer(invo ->
                    AccountMapper.entityToAdminResponse(invo.getArgument(0,Account.class)));

            assertEquals("admin@gmail.com", accountServiceImpl.getById(1L).getEmail());
            verify(accountRepository, times(1)).findById(anyLong());
        }

    }

    @Nested
    class RegisterModuleTest{

        @BeforeEach
        void setUp(){
            when(personRepository.findById(anyLong())).thenReturn(Optional.of(new PersonBuilder()
                        .firstName("one")
                        .lastName("1one")
                        .phoneNumber(givenPersonRequestDtoTwo().orElseThrow().getPhoneNumber())
                        .birthDate(givenPersonRequestDtoTwo().orElseThrow().getBirthDate())
                        .gender(givenPersonRequestDtoTwo().orElseThrow().getGender())
                        .id(1L)
                        .build()));
            when(accountRepository.save(any())).thenAnswer(arg -> arg.getArgument(0));
            when(accountValidationService.settleResponseType(any(Account.class))).thenAnswer(invo ->{
                Account account = invo.getArgument(0, Account.class);
                if(account.getRoles().stream().filter(role -> role.getName().equals("ROLE_ADMIN")).findFirst().isPresent()){
                    return AccountMapper.entityToAdminResponse(account);
                }
                return AccountMapper.entityToResponse(account);
            });
        }

        @DisplayName("Testing role user assignment")
        @Test
        void testAssignRoleUser() {
            when(roleRepository.findByName("ROLE_USER")).thenReturn(givenRoleUser());
            AccountRequestDto accountUserDto = givenUserAccountRequestDto().orElseThrow();

            //One role
            assertTrue(accountServiceImpl.save(accountUserDto) instanceof AccountUserResponseDto);
            verify(roleRepository, times(1)).findByName(anyString());
        }

        @DisplayName("Testing role user and admin assignment")
        @Test
        void testAssignRoleAdmin() {
            when(roleRepository.findByName("ROLE_USER")).thenReturn(givenRoleUser());
            when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(givenRoleAdmin());
            AccountRequestDto accountAdminDto = givenAdminAccountRequestDto().orElseThrow();

            AccountAdminResponseDto accountAdminResponseDto = (AccountAdminResponseDto) accountServiceImpl.save(accountAdminDto);


            //Both roles
            assertEquals(Arrays.asList(givenRoleResponseDtoUser().orElseThrow(), givenRoleResponseDtoAdmin().orElseThrow()), accountAdminResponseDto.getRoles());
            verify(roleRepository, times(2)).findByName(anyString());
        }

        @DisplayName("Testing service person assignment")
        @Test
        void testAssignPerson(){
            when(roleRepository.findByName("ROLE_USER")).thenReturn(givenRoleUser());
            when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(givenRoleAdmin());
            AccountRequestDto accountCreateDto = givenAdminAccountRequestDto().orElseThrow();

            AccountAdminResponseDto accountAdminResponseDto = (AccountAdminResponseDto) accountServiceImpl.save(accountCreateDto);
            PersonResponseDto answer = PersonMapper.entityToResponse(PersonMapper.requestToEntity(givenPersonRequestDtoOne().orElseThrow()));
            answer.setId(1L);
            //Per person test
            assertNotNull(accountAdminResponseDto.getPerson());
            verify(personRepository, times(1)).findById(anyLong());
            assertEquals(answer, (accountAdminResponseDto.getPerson()));
        }

    }




}
