package com.crhistianm.springboot.gallo.springboot_gallo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
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
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.*;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountAdminResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountCreateDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountUserResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.AccountMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.PersonMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.PersonRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplUnitTest {

    @Mock
    AccountRepository accountRepository;

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
    class RegisterModuleTest{

        @BeforeEach
        void setUp(){
            when(personRepository.findById(anyLong())).thenReturn(Optional.of(new PersonBuilder()
                        .firstName("one")
                        .lastName("1one")
                        .phoneNumber(createPersonTwoDto().orElseThrow().getPhoneNumber())
                        .birthDate(createPersonTwoDto().orElseThrow().getBirthDate())
                        .gender(createPersonTwoDto().orElseThrow().getGender())
                        .id(1L)
                        .build()));
            when(accountRepository.save(any())).thenAnswer(arg -> arg.getArgument(0));
        }

        @DisplayName("Testing role user assignment")
        @Test
        void testAssignRoleUser() {
            when(roleRepository.findByName("ROLE_USER")).thenReturn(createUserRole());
            AccountCreateDto accountUserDto = createAccountDto().orElseThrow();

            //One role
            assertTrue(accountServiceImpl.save(accountUserDto) instanceof AccountUserResponseDto);
            verify(roleRepository, times(1)).findByName(anyString());
        }

        @DisplayName("Testing role user and admin assignment")
        @Test
        void testAssignRoleAdmin() {
            when(roleRepository.findByName("ROLE_USER")).thenReturn(createUserRole());
            when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(createAdminRole());
            AccountCreateDto accountAdminDto = createAccountAdminDto().orElseThrow();

            AccountResponseDto accountResponseDto = accountServiceImpl.save(accountAdminDto);

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


            AccountAdminResponseDto accountAdminResponseDto = (AccountAdminResponseDto) accountServiceImpl.save(accountCreateDto);
            Person answer = PersonMapper.createToEntity(createPersonOneDto().orElseThrow());
            answer.setId(1L);
            //Per person test
            assertNotNull(accountAdminResponseDto.getPerson());
            verify(personRepository, times(1)).findById(anyLong());
            assertEquals(answer, (accountAdminResponseDto.getPerson()));
        }

    }

    @Nested
    class ValidationModuleTest{

        @Nested
        class IsEmailAvailableTest{

            @BeforeEach
            void setUp(){
                when(accountRepository.existsByEmail(anyString())).thenAnswer(invo -> {
                    return invo.getArgument(0).equals("came.29meca@gmail.com");
                });

            }

            @Test
            void testAvailable(){
                assertFalse(accountServiceImpl.isEmailAvailable("came.29meca@gmail.com"));
                verify(accountRepository, times(1)).existsByEmail(anyString());
            }

            @Test
            void testNotAvailable(){
                assertTrue(accountServiceImpl.isEmailAvailable("example@gmail.com"));
                verify(accountRepository, times(1)).existsByEmail(anyString());
            }

        }

        @Nested
        class IsPersonIdAssignedTest{

            @BeforeEach
            void setUp(){
                when(accountRepository.findAccountByPersonId(anyLong())).thenAnswer(invo ->{
                    Optional<Account> account = Optional.empty();
                    if(invo.getArgument(0, Long.class) == 1L) account = Optional.of(AccountMapper.createToEntity(createAccountDto().orElseThrow()));
                    return account;
                });
            }

            @Test
            void testNotAssigned(){
                assertFalse(accountServiceImpl.isPersonIdAssigned(2L));
                verify(accountRepository, times(1)).findAccountByPersonId(anyLong());
            }

            @Test
            void testAssigned(){
                assertTrue(accountServiceImpl.isPersonIdAssigned(1L));
                verify(accountRepository, times(1)).findAccountByPersonId(anyLong());
            }

        }

    }





}
