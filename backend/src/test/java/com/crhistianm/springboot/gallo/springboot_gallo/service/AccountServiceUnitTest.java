package com.crhistianm.springboot.gallo.springboot_gallo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.hibernate.mapping.Array;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.AccountBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.builder.FieldInfoErrorBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.builder.PersonBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.builder.RoleBuilder;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.*;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountAdminResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountUpdateRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountUserResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.RoleRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.RoleResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Audit;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.AccountMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.PersonMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.PersonRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.RoleRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.validation.service.AccountValidator;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class AccountServiceUnitTest {

    @Mock
    AccountValidator accountValidator;

    @Mock
    AccountRepository accountRepository;

    @Mock
    AccountValidationService accountValidationService;

    @Mock
    RoleRepository roleRepository;

    @Mock
    EntityManager entityManager;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    AccountService accountService;

    @Nested
    class ViewModuleTest{

        @BeforeEach
        void setUp(){
            lenient().doAnswer(invo -> {
                FieldInfoError field = null;
                if(invo.getArgument(0, Long.class).equals(120L)) {
                    field = new FieldInfoErrorBuilder().name("error").build();
                    throw new ValidationServiceException(new ArrayList<>(List.of(field)));
                }
                return Optional.ofNullable(field);
            }).when(accountValidator).validateByIdRequest(anyLong());
            lenient().when(accountRepository.findById(anyLong())).thenAnswer(invo -> {
                Optional<Account> accountOptional = Optional.empty();
                if(invo.getArgument(0, Long.class) == 1L) accountOptional = givenAccountEntityAdmin();
                if(invo.getArgument(0, Long.class) == 120L) {
                    Account account = givenAccountEntityAdmin().orElseThrow();
                    account.getPerson().setId(120L);
                    accountOptional = Optional.of(account);
                }
                return accountOptional;
            });
        }

        @Test
        void testGetAll(){
            when(accountRepository.findAll()).thenReturn(List.of(givenAccountEntityAdmin().orElseThrow(), givenAccountEntityUser().orElseThrow()));
            List<AccountAdminResponseDto> expectedList = List.of((AccountAdminResponseDto)AccountMapper.entityToAdminResponse(givenAccountEntityAdmin().orElseThrow()), 
                    (AccountAdminResponseDto)AccountMapper.entityToAdminResponse(givenAccountEntityUser().orElseThrow()));
            assertEquals(expectedList, accountService.getAll());
            verify(accountRepository, times(1)).findAll();
        }

        @Test
        void testGetByIdNotFound(){
            assertThrows(NotFoundException.class, () -> {
                accountService.getById(2L);
            });
            verify(accountRepository, times(1)).findById(anyLong());
            verifyNoInteractions(accountValidator);
            verifyNoInteractions(accountValidationService);
        }

        @Test
        void testGetById(){
            when(accountValidationService.settleResponseType(any(Account.class))).thenAnswer(invo ->
                    AccountMapper.entityToAdminResponse(invo.getArgument(0,Account.class)));

            assertEquals("admin@gmail.com", accountService.getById(1L).getEmail());
            verify(accountRepository, times(1)).findById(anyLong());
            verify(accountValidationService, times(1)).settleResponseType(any(Account.class));
        }

        @Test
        void shouldThrowExceptionWhenRequestByIdIsInvalid() {
            FieldInfoError field = null;

            field = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> accountService.getById(120L)).actual().getFieldErrors().get(0);
            assertThat(field).isNotNull();
            assertThat(field).extracting(FieldInfoError::getName).isEqualTo("error");

            verify(accountRepository, times(1)).findById(eq(120L));
            verify(accountValidator).validateByIdRequest(eq(120L));
            verifyNoInteractions(accountValidationService);
        }

    }

    @Nested
    class RegisterModuleTest{

        @BeforeEach
        void setUp(){
            doAnswer(invo -> {
                if(invo.getArgument(0, AccountRequestDto.class).getPersonId().equals(10L)) {
                    throw new ValidationServiceException();
                }
                return null;
            }).when(accountValidator).validateRequest(any(AccountRequestDto.class));
            lenient().when(entityManager.getReference(Mockito.<Class<Person>>any(), anyLong())).thenReturn(new PersonBuilder()
                        .firstName("one")
                        .lastName("1one")
                        .phoneNumber(givenPersonRequestDtoTwo().orElseThrow().getPhoneNumber())
                        .birthDate(givenPersonRequestDtoTwo().orElseThrow().getBirthDate())
                        .gender(givenPersonRequestDtoTwo().orElseThrow().getGender())
                        .id(1L)
                        .build());
            lenient().when(accountRepository.save(any())).thenAnswer(arg -> arg.getArgument(0));
            lenient().when(accountValidationService.settleResponseType(any(Account.class))).thenAnswer(invo ->{
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
            assertTrue(accountService.save(accountUserDto) instanceof AccountUserResponseDto);
            verify(roleRepository, times(1)).findByName(anyString());
        }

        @DisplayName("Testing role user and admin assignment")
        @Test
        void testAssignRoleAdmin() {
            when(roleRepository.findByName("ROLE_USER")).thenReturn(givenRoleUser());
            when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(givenRoleAdmin());
            AccountRequestDto accountAdminDto = givenAdminAccountRequestDto().orElseThrow();

            AccountAdminResponseDto accountAdminResponseDto = (AccountAdminResponseDto) accountService.save(accountAdminDto);


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

            AccountAdminResponseDto accountAdminResponseDto = (AccountAdminResponseDto) accountService.save(accountCreateDto);
            //Per person test
            assertNotNull(accountAdminResponseDto.getPersonId());
            verify(entityManager, times(1)).getReference(any(), anyLong());
            assertThat(accountAdminResponseDto).extracting(AccountAdminResponseDto::getPersonId).isEqualTo(1L);
        }

        @Test
        void shouldThrowExceptionWhenUserRequestIsInvalid() {
            AccountRequestDto requestDto = givenUserAccountRequestDto().orElseThrow();
            requestDto.setPersonId(10L);

            assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> accountService.save(requestDto));
        }

    }

    @Nested
    class UpdateModuleTest {

        AccountUpdateRequestDto accountDto;

        @BeforeEach
        void setUp() {
            accountDto = new AccountUpdateRequestDto();

            lenient().doAnswer(invo -> {
                Account account = null;
                if(invo.getArgument(0, Long.class).equals(1L)) account = new AccountBuilder().id(1L).person(new PersonBuilder().id(1L).build()).build();
                return Optional.ofNullable(account); 
            }).when(accountRepository).findById(anyLong());

            lenient().doAnswer(invo -> {
                String arg = invo.getArgument(1, AccountUpdateRequestDto.class).getEmail();
                if(arg != null && arg.equals("exception")){
                    throw new ValidationServiceException("exception");
                };
                return null;
            }).when(accountValidator).validateUpdateRequest(anyLong(), any(AccountUpdateRequestDto.class), anyLong());

            lenient().doAnswer(invo -> {
                return new PersonBuilder().id(10L).firstName("10person").build();
            }).when(entityManager).getReference(Mockito.<Class<Person>>any(), anyLong());

            lenient().doAnswer(invo ->{
                return invo.getArgument(0, Account.class);
            }).when(accountRepository).save(any(Account.class));

            lenient().doAnswer(invo -> {
                return AccountMapper.entityToAdminResponse(invo.getArgument(0, Account.class));
            }).when(accountValidationService).settleResponseType(any(Account.class));

            lenient().doAnswer(invo ->{
                return "encoded".concat(invo.getArgument(0, String.class));
            }).when(passwordEncoder).encode(anyString());

        }

        @Nested
        class NotFoundExceptionTest{

            @Test
            void shouldThrowExceptionWhenAccountPathIdIsNotFound() {
                String message = assertThatExceptionOfType(NotFoundException.class)
                    .isThrownBy(() -> accountService.update(2L, accountDto)).actual().getMessage();

                assertThat(message).isEqualTo("Account not found");

                verify(accountRepository, times(1)).findById(2L);

                verifyNoInteractions(accountValidator);
                verifyNoInteractions(entityManager);
                verifyNoInteractions(accountValidationService);
                verify(accountRepository, times(0)).save(any(Account.class));
            }

            @Test
            void shouldNotThrowExceptionWhenAccountPathIdIsFound() {
                assertDoesNotThrow(() -> accountService.update(1L, accountDto));

                verify(accountRepository, times(1)).findById(1L);

                verifyNoInteractions(entityManager);
                verify(accountValidator, times(1)).validateUpdateRequest(eq(1L), eq(accountDto), eq(1L));
                verify(accountValidationService, times(1)).settleResponseType(any(Account.class));
                verify(accountRepository, times(1)).save(any(Account.class));
            }

        }

        @Nested
        class ValidateUpdateRequestTest{

            @Test
            void shouldThrowExceptionWhenIsNotValid() {
                accountDto.setEmail("exception");

                String message = assertThatExceptionOfType(ValidationServiceException.class)
                    .isThrownBy(() -> accountService.update(1L, accountDto)).actual().getMessage();

                assertThat(message).isEqualTo("exception");

                verify(accountValidator, times(1)).validateUpdateRequest(eq(1L), eq(accountDto), eq(1L));
                verify(accountRepository, times(1)).findById(1L);

                verifyNoInteractions(accountValidationService);
                verifyNoInteractions(entityManager);
            }

            @Test
            void shouldNotThrowExceptionWhenIsValid() {
                accountDto.setEmail("noexception");

                assertDoesNotThrow(() -> accountService.update(1L, accountDto));

                verify(accountValidator, times(1)).validateUpdateRequest(eq(1L), eq(accountDto), eq(1L));
                verify(accountRepository, times(1)).findById(1L);

                verify(accountValidationService).settleResponseType(any(Account.class));
                verify(accountRepository).save(any(Account.class));

                verifyNoInteractions(entityManager);
            }



        }

        @Nested
        class FieldConditionsTest {

            AccountAdminResponseDto expectedResponse;

            @BeforeEach
            void setUp() {
                expectedResponse = new AccountAdminResponseDto();
            }

            @AfterEach
            void verifyCommonInteractions(){
                verify(accountRepository, times(1)).findById(1L);
                verify(accountValidator, times(1)).validateUpdateRequest(eq(1L), eq(accountDto), eq(1L));
                verify(accountRepository, times(1)).save(any(Account.class));
                verify(accountValidationService, times(1)).settleResponseType(any(Account.class));
            }

            @Test
            void shouldReturnPersistedAccountWhenAllFieldsAreEmpty() {
                AccountAdminResponseDto expectedResponse = (AccountAdminResponseDto) accountService.update(1L, accountDto);

                assertThat(expectedResponse.getId()).isEqualTo(1L);
                assertThat(expectedResponse.getPersonId()).isEqualTo(1L);
                assertThat(expectedResponse.getEmail()).isNull();
                assertThat(expectedResponse.getAudit().isEnabled()).isEqualTo(false);
                assertThat(expectedResponse.getRoles()).isEmpty();

                verifyNoInteractions(entityManager);
                verifyNoInteractions(passwordEncoder);
            }

            @Test
            void shouldReturnUpdatedAccountWhenAllFieldsAreFilled() {
                accountDto.setPersonId(10L);
                accountDto.setEmail("example@gmail.com");
                accountDto.setEnabled(true);
                accountDto.setPassword("12345");
                accountDto.setRoles(List.of(new RoleRequestDto(1L, "role1"), new RoleRequestDto(2L, "role2")));

                expectedResponse = (AccountAdminResponseDto) accountService.update(1L, accountDto);

                verify(passwordEncoder, times(1)).encode(eq("12345"));

                assertThat(expectedResponse.getPersonId()).isEqualTo(10L);
                assertThat(expectedResponse.getEmail()).isEqualTo("example@gmail.com");
                assertThat(expectedResponse.getAudit().isEnabled()).isEqualTo(true);
                assertThat(expectedResponse.getRoles()).hasSize(2);

                Optional<RoleResponseDto> roleOne = expectedResponse.getRoles().stream().filter(r->r.getName().equals("role1")).findFirst();
                Optional<RoleResponseDto> roleTwo = expectedResponse.getRoles().stream().filter(r->r.getName().equals("role2")).findFirst();

                assertThat(roleOne).isNotEmpty();
                assertThat(roleTwo).isNotEmpty();

                verify(entityManager, times(1)).getReference(eq(Person.class), eq(10L));
            }

            @Test
            void shouldAssignPersonToAccountWhenPersonIdIsNotNull() {
                accountDto.setPersonId(10L);

                expectedResponse = (AccountAdminResponseDto) accountService.update(1L, accountDto);

                assertThat(expectedResponse.getPersonId()).isEqualTo(10L);

                verify(entityManager, times(1)).getReference(eq(Person.class), eq(10L));

                verifyNoInteractions(passwordEncoder);
            }

            @Test
            void shouldAssignEmailWhenEmailIsNotNull() {
                accountDto.setEmail("example@gmail.com");

                expectedResponse = (AccountAdminResponseDto) accountService.update(1L, accountDto);

                assertThat(expectedResponse.getEmail()).isNotNull();
                assertThat(expectedResponse.getEmail()).isEqualTo("example@gmail.com");
                

                verifyNoInteractions(passwordEncoder);
                verifyNoInteractions(entityManager);
            }

            @Test
            void shouldAssignEnabledWhenEnabledIsNotNull() {
                accountDto.setEnabled(true);

                expectedResponse = (AccountAdminResponseDto) accountService.update(1L, accountDto);

                assertThat(expectedResponse.getAudit().isEnabled()).isNotNull();
                assertThat(expectedResponse.getAudit().isEnabled()).isEqualTo(true);
                //assertThat(expectedResponse.getAudit().isEnabled()).isTrue();

                verifyNoInteractions(passwordEncoder);
                verifyNoInteractions(entityManager);
            }

            @Test
            void shouldAssignPasswordWhenPasswordIsNotNull() {
                accountDto.setPassword("12345");

                expectedResponse = (AccountAdminResponseDto) accountService.update(1L, accountDto);

                verify(passwordEncoder, times(1)).encode(eq("12345"));

                verifyNoInteractions(entityManager);
            }

            @Test
            void shouldAssignRolesWhenRolesAreNotEmpty() {
                accountDto.setRoles(List.of(new RoleRequestDto(1L, "role1"), new RoleRequestDto(2L, "role2")));

                expectedResponse = (AccountAdminResponseDto) accountService.update(1L, accountDto);

                assertThat(expectedResponse.getRoles()).extracting(RoleResponseDto::getId, RoleResponseDto::getName)
                    .containsExactly(tuple(1L, "role1"),
                                    (tuple(2L, "role2")));

                verifyNoInteractions(passwordEncoder);
                verifyNoInteractions(entityManager);
            }



        }


    }

    @Nested
    class DeleteModuleTest {

        @BeforeEach
        void setUp() {

            doAnswer(invo -> {
                Account account = null;
                if(invo.getArgument(0, Long.class).equals(1L)) {
                    account = givenAccountEntityUser().orElseThrow();
                    account.getPerson().setId(1L);
                }
                if(invo.getArgument(0, Long.class).equals(2L)) {
                    account = givenAccountEntityAdmin().orElseThrow();
                    account.getPerson().setId(2L);
                }
                if(invo.getArgument(0, Long.class).equals(10L)) {
                    account = givenAccountEntityAdmin().orElseThrow();
                    account.getPerson().setId(1L);
                    account.setEmail("settleadmin");
                }
                return Optional.ofNullable(account);
            }).when(accountRepository).findById(anyLong());

            lenient().doAnswer(invo -> {
                if(invo.getArgument(0, Long.class).equals(2L)) throw new ValidationServiceException();
                return null;
            }).when(accountValidator).validateByIdRequest(anyLong());

            lenient().doAnswer(invo -> {
                Account account = invo.getArgument(0, Account.class);
                if(account.getEmail().equals("settleadmin")) return AccountMapper.entityToAdminResponse(account);
                return AccountMapper.entityToResponse(account);
            }).when(accountValidationService).settleResponseType(any(Account.class));

        }

        @Test
        void shouldReturnAdminResponseDtoWhenDeleteIsSuccessfull() {
            AccountAdminResponseDto adminResponse = (AccountAdminResponseDto) accountService.delete(10L);

            assertThat(adminResponse).extracting(AccountAdminResponseDto::getAudit)
                .extracting(Audit::getUpdatedAt, Audit::getCreatedAt, Audit::isEnabled)
                .contains(
                            LocalDateTime.of(2004, 01, 01, 1, 1, 1, 1), 
                            LocalDateTime.of(2004, 02, 01, 1, 1, 1, 1),
                            true);

            assertThat(adminResponse).extracting(AccountAdminResponseDto::getId).isEqualTo(1L);
            assertThat(adminResponse).extracting(AccountAdminResponseDto::getEmail).isEqualTo("settleadmin");
            assertThat(adminResponse.getRoles()).extracting(RoleResponseDto::getName)
                .containsExactly("ROLE_ADMIN", "ROLE_USER");

            assertThat(adminResponse).extracting(AccountAdminResponseDto::getPersonId).isEqualTo(1L);

            verify(accountRepository, times(1)).findById(eq(10L));
            verify(accountValidator, times(1)).validateByIdRequest(eq(1L));
            verify(accountRepository, times(1)).delete(any(Account.class));
            verify(accountValidationService, times(1)).settleResponseType(any(Account.class));
        }

        @Test
        void shouldReturnUserResponseDtoWhenDeleteIsSuccessfull() {

            AccountUserResponseDto userResponse = (AccountUserResponseDto) accountService.delete(1L);

            assertThat(userResponse).extracting(AccountUserResponseDto::getEmail).isEqualTo("user@gmail.com");

            verify(accountRepository, times(1)).findById(eq(1L));
            verify(accountValidator, times(1)).validateByIdRequest(eq(1L));
            verify(accountRepository, times(1)).delete(any(Account.class));
            verify(accountValidationService, times(1)).settleResponseType(any(Account.class));
        }

        @Test
        void shouldThrowExceptionWhenRequestIsInvalid() {
            assertThatExceptionOfType(ValidationServiceException.class).isThrownBy(()-> accountService.delete(2L));
            verify(accountRepository, times(1)).findById(eq(2L));
            verify(accountValidator, times(1)).validateByIdRequest(eq(2L));
            verifyNoMoreInteractions(accountRepository);
            verifyNoInteractions(accountValidationService);
        }

        @Test
        void shouldThrowExceptionWhenAccountPathIdIsNotFound() {
            String errorMessage = assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> accountService.delete(100L)).actual().getMessage();

            assertThat(errorMessage).isEqualTo("Account not found");

            verify(accountRepository, times(1)).findById(100L);
            verifyNoMoreInteractions(accountRepository);
            verifyNoInteractions(accountValidator);
            verifyNoInteractions(accountValidationService);
        }


    }





}
