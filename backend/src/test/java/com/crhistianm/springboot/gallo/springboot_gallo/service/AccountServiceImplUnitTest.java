package com.crhistianm.springboot.gallo.springboot_gallo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.RoleRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.RoleResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
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

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    IdentityVerificationService identityService;

    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    @Nested
    class ViewModuleTest{

        @BeforeEach
        void setUp(){
            lenient().doAnswer(invo -> {
                FieldInfoError field = null;
                if(invo.getArgument(0, Long.class).equals(120L)) field = new FieldInfoErrorBuilder().name("identity").build();
                return Optional.ofNullable(field);
            }).when(identityService).validateUserAllowance(anyLong());
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

        @Test
        void shouldThrowExceptionWhenUserAllowanceIsInvalid() {
            FieldInfoError field = null;

            field = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> accountServiceImpl.getById(120L)).actual().getFieldErrors().get(0);
            assertThat(field).isNotNull();
            assertThat(field).extracting(FieldInfoError::getName).isEqualTo("identity");

            verify(accountRepository, times(1)).findById(eq(120L));
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
                Person person = null;
                if(invo.getArgument(0, Long.class).equals(10L)) person = new PersonBuilder().id(10L).firstName("10person").build();
                return Optional.ofNullable(person);
            }).when(personRepository).findById(anyLong());

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
                    .isThrownBy(() -> accountServiceImpl.update(2L, accountDto)).actual().getMessage();

                assertThat(message).isEqualTo("Account not found");

                verify(accountRepository, times(1)).findById(2L);

                verifyNoInteractions(accountValidator);
                verifyNoInteractions(personRepository);
                verifyNoInteractions(accountValidationService);
                verify(accountRepository, times(0)).save(any(Account.class));
            }

            @Test
            void shouldNotThrowExceptionWhenAccountPathIdIsFound() {
                assertDoesNotThrow(() -> accountServiceImpl.update(1L, accountDto));

                verify(accountRepository, times(1)).findById(1L);

                verifyNoInteractions(personRepository);
                verify(accountValidator, times(1)).validateUpdateRequest(eq(1L), eq(accountDto), eq(1L));
                verify(accountValidationService, times(1)).settleResponseType(any(Account.class));
                verify(accountRepository, times(1)).save(any(Account.class));
            }

            @Test
            void shouldThrowExceptionWhenAccountPersonIdIsNotFound() {
                accountDto.setPersonId(2L);
                String message = assertThatExceptionOfType(NotFoundException.class)
                    .isThrownBy(() -> accountServiceImpl.update(1L, accountDto)).actual().getMessage();

                assertThat(message).isEqualTo("Person not found");
                verify(personRepository, times(1)).findById(eq(2L));

                verify(accountRepository, times(1)).findById(eq(1L));
                verifyNoInteractions(accountValidationService);
                verify(accountRepository, times(0)).save(any(Account.class));
            }

            @Test
            void shouldNotThrowExceptionWhenAccountPersonIdIsFound() {
                accountDto.setPersonId(10L);

                assertDoesNotThrow(() -> accountServiceImpl.update(1L, accountDto));

                verify(accountValidator).validateUpdateRequest(eq(1L), eq(accountDto), eq(1L));
                verify(personRepository, times(1)).findById(eq(10L));
                verify(accountValidationService).settleResponseType(any(Account.class));
                verify(accountRepository).save(any(Account.class));
            }

        }

        @Nested
        class ValidateUpdateRequestTest{

            @Test
            void shouldThrowExceptionWhenIsNotValid() {
                accountDto.setEmail("exception");

                String message = assertThatExceptionOfType(ValidationServiceException.class)
                    .isThrownBy(() -> accountServiceImpl.update(1L, accountDto)).actual().getMessage();

                assertThat(message).isEqualTo("exception");

                verify(accountValidator, times(1)).validateUpdateRequest(eq(1L), eq(accountDto), eq(1L));
                verify(accountRepository, times(1)).findById(1L);

                verifyNoInteractions(accountValidationService);
                verifyNoInteractions(personRepository);
            }

            @Test
            void shouldNotThrowExceptionWhenIsValid() {
                accountDto.setEmail("noexception");

                assertDoesNotThrow(() -> accountServiceImpl.update(1L, accountDto));

                verify(accountValidator, times(1)).validateUpdateRequest(eq(1L), eq(accountDto), eq(1L));
                verify(accountRepository, times(1)).findById(1L);

                verify(accountValidationService).settleResponseType(any(Account.class));
                verify(accountRepository).save(any(Account.class));

                verifyNoInteractions(personRepository);
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
                AccountAdminResponseDto expectedResponse = (AccountAdminResponseDto) accountServiceImpl.update(1L, accountDto);

                assertThat(expectedResponse.getId()).isEqualTo(1L);
                assertThat(expectedResponse.getPerson().getId()).isEqualTo(1L);
                assertThat(expectedResponse.getEmail()).isNull();
                assertThat(expectedResponse.getAudit().isEnabled()).isEqualTo(false);
                assertThat(expectedResponse.getRoles()).isEmpty();

                verifyNoInteractions(personRepository);
                verifyNoInteractions(passwordEncoder);
            }

            @Test
            void shouldReturnUpdatedAccountWhenAllFieldsAreFilled() {
                accountDto.setPersonId(10L);
                accountDto.setEmail("example@gmail.com");
                accountDto.setEnabled(true);
                accountDto.setPassword("12345");
                accountDto.setRoles(List.of(new RoleRequestDto(1L, "role1"), new RoleRequestDto(2L, "role2")));

                expectedResponse = (AccountAdminResponseDto) accountServiceImpl.update(1L, accountDto);

                verify(passwordEncoder, times(1)).encode(eq("12345"));

                assertThat(expectedResponse.getPerson().getId()).isEqualTo(10L);
                assertThat(expectedResponse.getPerson().getFirstName()).isEqualTo("10person");
                assertThat(expectedResponse.getEmail()).isEqualTo("example@gmail.com");
                assertThat(expectedResponse.getAudit().isEnabled()).isEqualTo(true);
                assertThat(expectedResponse.getRoles()).hasSize(2);

                Optional<RoleResponseDto> roleOne = expectedResponse.getRoles().stream().filter(r->r.getName().equals("role1")).findFirst();
                Optional<RoleResponseDto> roleTwo = expectedResponse.getRoles().stream().filter(r->r.getName().equals("role2")).findFirst();

                assertThat(roleOne).isNotEmpty();
                assertThat(roleTwo).isNotEmpty();

                verify(personRepository, times(1)).findById(eq(10L));
            }

            @Test
            void shouldAssignPersonToAccountWhenPersonIdIsNotNull() {
                accountDto.setPersonId(10L);

                expectedResponse = (AccountAdminResponseDto) accountServiceImpl.update(1L, accountDto);

                assertThat(expectedResponse.getPerson().getId()).isEqualTo(10L);
                assertThat(expectedResponse.getPerson().getFirstName()).isEqualTo("10person");


                verify(personRepository, times(1)).findById(eq(10L));

                verifyNoInteractions(passwordEncoder);
            }

            @Test
            void shouldAssignEmailWhenEmailIsNotNull() {
                accountDto.setEmail("example@gmail.com");

                expectedResponse = (AccountAdminResponseDto) accountServiceImpl.update(1L, accountDto);

                assertThat(expectedResponse.getEmail()).isNotNull();
                assertThat(expectedResponse.getEmail()).isEqualTo("example@gmail.com");
                

                verifyNoInteractions(passwordEncoder);
                verifyNoInteractions(personRepository);
            }

            @Test
            void shouldAssignEnabledWhenEnabledIsNotNull() {
                accountDto.setEnabled(true);

                expectedResponse = (AccountAdminResponseDto) accountServiceImpl.update(1L, accountDto);

                assertThat(expectedResponse.getAudit().isEnabled()).isNotNull();
                assertThat(expectedResponse.getAudit().isEnabled()).isEqualTo(true);
                //assertThat(expectedResponse.getAudit().isEnabled()).isTrue();

                verifyNoInteractions(passwordEncoder);
                verifyNoInteractions(personRepository);
            }

            @Test
            void shouldAssignPasswordWhenPasswordIsNotNull() {
                accountDto.setPassword("12345");

                expectedResponse = (AccountAdminResponseDto) accountServiceImpl.update(1L, accountDto);

                verify(passwordEncoder, times(1)).encode(eq("12345"));

                verifyNoInteractions(personRepository);
            }

            @Test
            void shouldAssignRolesWhenRolesAreNotEmpty() {
                accountDto.setRoles(List.of(new RoleRequestDto(1L, "role1"), new RoleRequestDto(2L, "role2")));

                expectedResponse = (AccountAdminResponseDto) accountServiceImpl.update(1L, accountDto);

                assertThat(expectedResponse.getRoles()).extracting(RoleResponseDto::getId, RoleResponseDto::getName)
                    .containsExactly(tuple(1L, "role1"),
                                    (tuple(2L, "role2")));

                verifyNoInteractions(passwordEncoder);
                verifyNoInteractions(personRepository);
            }



        }


    }




}
