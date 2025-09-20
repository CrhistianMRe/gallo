package com.crhistianm.springboot.gallo.springboot_gallo.service;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenAccountEntityAdmin;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.AccountBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.builder.PersonBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountAdminResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountUserResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
public class AccountValidationServiceUnitTest {

    @Mock
    AccountRepository accountRepository;
    
    @Mock
    IdentityVerificationService identityService;

    @Mock
    PersonValidationService personValidationService;

    @InjectMocks
    AccountValidationServiceImpl accountValidationService;

    @Spy
    @InjectMocks
    AccountValidationServiceImpl spyAccountValidationService;

    @Nested
    class SettleResponseTypeTest {

        @Test
        void returnsAccountAdminResponseDtoWhenLoggedInUserHasAdminAuthority() {
            when(identityService.isAdminAuthority()).thenReturn(true);
            assertTrue(accountValidationService.settleResponseType(givenAccountEntityAdmin().orElseThrow()) instanceof AccountAdminResponseDto);
        }

        @Test
        void returnsAccountResponseDtoWhenLoggedInUserOnlyHasUserAuthority() {
            when(identityService.isAdminAuthority()).thenReturn(false);
            assertTrue(accountValidationService.settleResponseType(givenAccountEntityAdmin().orElseThrow()) instanceof AccountUserResponseDto);
        }

    }

    @Nested
    class IsEmailAvailableTest {

        @BeforeEach
        void setUp(){
            lenient().doAnswer(invo -> {
                return invo.getArgument(0, String.class).equals("exists@gmail.com");
            }).when(accountRepository).existsByEmail(anyString());

        }

        @Nested
        class CreateRequestTest {

            @Test
            void returnsFalseWhenRequestEmailExistsInDb(){
                assertThat(accountValidationService.isEmailAvailable(null, "doesnotexist@gmail.com")).isTrue();
                verify(accountRepository, times(1)).existsByEmail(anyString());
                verify(accountRepository, times(0)).findById(anyLong());
            }

            @Test
            void returnsTrueWhenRequestEmailDoesNotExistInDb() {
                assertThat(accountValidationService.isEmailAvailable(null, "exists@gmail.com")).isFalse();
                verify(accountRepository, times(1)).existsByEmail(anyString());
                verify(accountRepository, times(0)).findById(anyLong());
            }

        }

        @Nested
        class UpdateRequestTest {

            @BeforeEach
            void setUp(){
                lenient().doReturn(givenAccountEntityAdmin())
                .when(accountRepository).findById(anyLong());
            }

            @Test
            void returnsTrueWhenRequestEmailMatchesDbAccountEmail() {
                assertThat(accountValidationService.isEmailAvailable(1L, "admin@gmail.com")).isTrue();
                verify(accountRepository, times(0)).existsByEmail(anyString());
                verify(accountRepository, times(1)).findById(anyLong());
            } 

            @Test
            void returnsTrueWhenRequestEmailMisMatchDbAccountEmailAndIsNotInDb() {
                assertThat(accountValidationService.isEmailAvailable(1L, "doesnotexists@gmail.com")).isTrue();
                verify(accountRepository, times(1)).existsByEmail(anyString());
                verify(accountRepository, times(1)).findById(anyLong());
            } 

            @Test
            void returnsFalseWhenRequestEmailMismatchDbAccountEmailAndIsInDb() {
                assertThat(accountValidationService.isEmailAvailable(1L, "exists@gmail.com")).isFalse();
                verify(accountRepository, times(1)).existsByEmail(anyString());
                verify(accountRepository, times(1)).findById(anyLong());
            } 

        }

        @Nested
        class IsPersonIdAssignedTest {

            @BeforeEach
            void setUp(){
                lenient().doAnswer(invo ->{
                    Optional<Account> accountOptional = Optional.empty();
                    if(invo.getArgument(0, Long.class).equals(1L)) accountOptional = givenAccountEntityAdmin();
                    return accountOptional;
                }).when(accountRepository).findById(anyLong());
            }

            @Nested
            class CreateRequestTest { 

                @Test
                void returnsTrueWhenAccountIsFoundByPersonId(){
                    assertThat(accountValidationService.isPersonIdAssigned(null, 1L));
                    verify(accountRepository, times(1)).findAccountByPersonId(anyLong());
                    verify(accountRepository, times(0)).findById(anyLong());
                }
                
                @Test
                void returnsFalseWhenAccountIsNotFoundByPersonId(){
                    assertThat(accountValidationService.isPersonIdAssigned(null, 2L));
                    verify(accountRepository, times(1)).findAccountByPersonId(anyLong());
                    verify(accountRepository, times(0)).findById(anyLong());
                }

            }

            @Nested
            class UpdateRequestTest {

                @BeforeEach
                void setUp(){
                    doReturn(Optional.of(new AccountBuilder().person(new PersonBuilder().id(3L).build()).build()))
                        .when(accountRepository).findById(anyLong());
                }

                @Test
                void returnsTrueWhenPersonIdMatchesDbPersonId() {
                    assertThat(accountValidationService.isPersonIdAssigned(1L, 3L));
                    verify(accountRepository, times(1)).findById(anyLong());
                    verify(accountRepository, times(0)).findAccountByPersonId(anyLong());
                }

                @Test
                void returnsTrueWhenPersonIdMismatchDbPersonIdAndAccountIsFoundByPersonId() {
                    assertThat(accountValidationService.isPersonIdAssigned(1L, 1L));
                    verify(accountRepository, times(1)).findById(anyLong());
                    verify(accountRepository, times(1)).findAccountByPersonId(anyLong());
                }

                @Test
                void returnsFalseWhenPersonIdMismatchDbPersonIdAndAccountIsNotFoundByPersonId() {
                    assertThat(accountValidationService.isPersonIdAssigned(1L, 2L));
                    verify(accountRepository, times(1)).findById(anyLong());
                    verify(accountRepository, times(1)).findAccountByPersonId(anyLong());
                }

            }

            @Nested
            class ValidateRequestTest {

                AccountRequestDto requestDto;

                List<FieldInfoError> fields;

                @BeforeEach
                void setUp(){
                    //nothing adds error 
                    requestDto = new AccountRequestDto();
                    fields = new ArrayList<>();
                    requestDto.setPersonId(1L); 
                    requestDto.setEmail("example@gmail.com"); 
                    lenient().doAnswer(invo ->{
                        return invo.getArgument(0, Long.class).equals(1L);
                    }).when(personValidationService).isPersonRegistered(anyLong());
                    lenient().doAnswer(invo -> {
                        return !invo.getArgument(1, Long.class).equals(1L);
                    }).when(spyAccountValidationService).isPersonIdAssigned(isNull(), anyLong());
                    lenient().doAnswer(invo -> {
                        return invo.getArgument(1, String.class).equals("example@gmail.com");
                    }).when(spyAccountValidationService).isEmailAvailable(isNull(), anyString());
                }

                @Test
                void shouldNotThrowExceptionWhenFieldsInfoErrorIsEmpty(){
                    assertDoesNotThrow(() -> spyAccountValidationService.validateRequest(requestDto));
                    verify(personValidationService, times(1)).isPersonRegistered(anyLong());
                    verify(spyAccountValidationService, times(1)).isPersonIdAssigned(isNull(), anyLong());
                    verify(spyAccountValidationService, times(1)).isEmailAvailable(isNull(), anyString());
                    verify(identityService, times(0)).isAdminAuthority();
                }

                @Test
                void shouldThrowExceptionWhenAllConditionsAreMet(){
                    doReturn(false).when(identityService).isAdminAuthority();
                    requestDto.setPersonId(2L);
                    requestDto.setEmail("badexample@gmail.com");
                    requestDto.setAdmin(true);

                    fields = assertThatExceptionOfType(ValidationServiceException.class)
                        .isThrownBy(() -> {
                            spyAccountValidationService.validateRequest(requestDto);
                        }).actual().getFieldErrors();

                    verify(personValidationService, times(1)).isPersonRegistered(anyLong());
                    verify(spyAccountValidationService, times(1)).isPersonIdAssigned(isNull(), anyLong());
                    verify(spyAccountValidationService, times(1)).isEmailAvailable(isNull(), anyString());
                    verify(identityService, times(1)).isAdminAuthority();

                    assertThat(fields).hasSize(4);

                    FieldInfoError fieldRegistered = fields.stream().filter(error -> error.getErrorMessage().contains("registered")).findFirst().orElseThrow();
                    FieldInfoError fieldAssigned = fields.stream().filter(error -> error.getErrorMessage().contains("assigned")).findFirst().orElseThrow();
                    FieldInfoError fieldEmail = fields.stream().filter(error -> error.getErrorMessage().contains("available")).findFirst().orElseThrow();
                    FieldInfoError fieldAdmin = fields.stream().filter(error -> error.getErrorMessage().contains("admin")).findFirst().orElseThrow();

                    //field registered
                    assertThat(fieldRegistered.getName()).isEqualTo("personId");
                    assertThat(fieldRegistered.getOwnerClass()).isEqualTo(AccountRequestDto.class);
                    assertThat(fieldRegistered.getErrorMessage()).isEqualTo("is not registered, register first!");
                    assertThat(fieldRegistered.getType()).isEqualTo(Long.class);
                    assertThat(fieldRegistered.getValue()).isEqualTo(2L);

                    //field assigned
                    assertThat(fieldAssigned.getName()).isEqualTo("personId");
                    assertThat(fieldAssigned.getOwnerClass()).isEqualTo(AccountRequestDto.class);
                    assertThat(fieldAssigned.getErrorMessage()).isEqualTo("is already assigned, use another person!");
                    assertThat(fieldAssigned.getType()).isEqualTo(Long.class);
                    assertThat(fieldAssigned.getValue()).isEqualTo(2L);

                    //field email
                    assertThat(fieldEmail.getName()).isEqualTo("email");
                    assertThat(fieldEmail.getOwnerClass()).isEqualTo(AccountRequestDto.class);
                    assertThat(fieldEmail.getErrorMessage()).isEqualTo("is not available, user another one!");
                    assertThat(fieldEmail.getType()).isEqualTo(String.class);
                    assertThat(fieldEmail.getValue()).isEqualTo("badexample@gmail.com");

                    //field admin
                    assertThat(fieldAdmin.getName()).isEqualTo("admin");
                    assertThat(fieldAdmin.getOwnerClass()).isEqualTo(AccountRequestDto.class);
                    assertThat(fieldAdmin.getErrorMessage()).isEqualTo("requires an admin user!");
                    assertThat(fieldAdmin.getType()).isPrimitive();
                    assertThat(String.valueOf(fieldAdmin.getType())).isEqualTo("boolean");
                    assertThat(fieldAdmin.getValue()).isEqualTo(true);
                }

                @Test
                void shouldThrowExceptionWhenPersonIsNotRegistered() {
                    doReturn(false).when(personValidationService).isPersonRegistered(anyLong());
                    requestDto.setPersonId(1L);
                    fields = assertThatExceptionOfType(ValidationServiceException.class)
                        .isThrownBy(()->{
                            spyAccountValidationService.validateRequest(requestDto);
                        }).actual().getFieldErrors();

                    FieldInfoError field = fields.get(0);

                    verify(personValidationService, times(1)).isPersonRegistered(anyLong());
                    verify(spyAccountValidationService, times(1)).isPersonIdAssigned(isNull(), anyLong());
                    verify(spyAccountValidationService, times(1)).isEmailAvailable(isNull(), anyString());
                    verify(identityService, times(0)).isAdminAuthority();

                    assertThat(fields).hasSize(1);
                    assertThat(field.getName()).isEqualTo("personId");
                    assertThat(field.getType()).isEqualTo(Long.class);
                    assertThat(field.getOwnerClass()).isEqualTo(AccountRequestDto.class);
                    assertThat(field.getErrorMessage()).isEqualTo("is not registered, register first!");
                    assertThat(field.getValue()).isEqualTo(1L);
                }

                @Test
                void shouldThrowExceptionWhenPersonIdIsAlreadyAssigned() {
                    doReturn(true).when(spyAccountValidationService).isPersonIdAssigned(isNull(), anyLong());

                    fields = assertThatExceptionOfType(ValidationServiceException.class)
                        .isThrownBy(() -> {
                            spyAccountValidationService.validateRequest(requestDto);
                        }).actual().getFieldErrors();

                    FieldInfoError field = fields.get(0);

                    verify(personValidationService, times(1)).isPersonRegistered(anyLong());
                    verify(spyAccountValidationService, times(1)).isPersonIdAssigned(isNull(), anyLong());
                    verify(spyAccountValidationService, times(1)).isEmailAvailable(isNull(), anyString());
                    verify(identityService, times(0)).isAdminAuthority();

                    assertThat(fields).hasSize(1);

                    assertThat(field.getName()).isEqualTo("personId");
                    assertThat(field.getType()).isEqualTo(Long.class);
                    assertThat(field.getOwnerClass()).isEqualTo(AccountRequestDto.class);
                    assertThat(field.getErrorMessage()).isEqualTo("is already assigned, use another person!");
                    assertThat(field.getValue()).isEqualTo(1L);
                }

                @Test
                void shouldThrowExceptionWhenEmailIsNotAvailable() {
                    doReturn(false).when(spyAccountValidationService).isEmailAvailable(isNull(), anyString());

                    fields = assertThatExceptionOfType(ValidationServiceException.class)
                        .isThrownBy(() -> {
                            spyAccountValidationService.validateRequest(requestDto);
                        }).actual().getFieldErrors();

                    FieldInfoError field = fields.get(0);

                    verify(personValidationService, times(1)).isPersonRegistered(anyLong());
                    verify(spyAccountValidationService, times(1)).isPersonIdAssigned(isNull(), anyLong());
                    verify(spyAccountValidationService, times(1)).isEmailAvailable(isNull(), anyString());
                    verify(identityService, times(0)).isAdminAuthority();

                    assertThat(fields).hasSize(1);

                    assertThat(field.getName()).isEqualTo("email");
                    assertThat(field.getType()).isEqualTo(String.class);
                    assertThat(field.getOwnerClass()).isEqualTo(AccountRequestDto.class);
                    assertThat(field.getErrorMessage()).isEqualTo("is not available, user another one!");
                    assertThat(field.getValue()).isEqualTo("example@gmail.com");
                }

                @Test
                void shouldThrowExceptionWhenFieldAdminIsTrueAndLoggedInUserIsNotAdmin(){
                    requestDto.setAdmin(true);
                    doReturn(false).when(identityService).isAdminAuthority();

                    fields = assertThatExceptionOfType(ValidationServiceException.class)
                        .isThrownBy(() -> {
                            spyAccountValidationService.validateRequest(requestDto);
                        }).actual().getFieldErrors();

                    FieldInfoError field = fields.get(0);

                    verify(personValidationService, times(1)).isPersonRegistered(anyLong());
                    verify(spyAccountValidationService, times(1)).isPersonIdAssigned(isNull(), anyLong());
                    verify(spyAccountValidationService, times(1)).isEmailAvailable(isNull(), anyString());
                    verify(identityService, times(1)).isAdminAuthority();

                    assertThat(fields).hasSize(1);

                    assertThat(field.getName()).isEqualTo("admin");
                    assertThat(field.getType()).isPrimitive();
                    assertThat(String.valueOf(field.getType())).isEqualTo("boolean");
                    assertThat(field.getOwnerClass()).isEqualTo(AccountRequestDto.class);
                    assertThat(field.getErrorMessage()).isEqualTo("requires an admin user!");
                    assertThat(field.getValue()).isEqualTo(true);
                }

            }

        }

    }
}

