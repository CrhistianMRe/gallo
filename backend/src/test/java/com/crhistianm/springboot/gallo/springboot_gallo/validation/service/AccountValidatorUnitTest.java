package com.crhistianm.springboot.gallo.springboot_gallo.validation.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.text.NumberFormat.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.FieldInfoErrorBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.service.AccountValidationService;
import com.crhistianm.springboot.gallo.springboot_gallo.service.IdentityVerificationService;
import com.crhistianm.springboot.gallo.springboot_gallo.service.PersonValidationService;

@ExtendWith(MockitoExtension.class)
public class AccountValidatorUnitTest {
    
    @Mock
    PersonValidationService personService; 

    @Mock
    AccountValidationService accountService;

    @Mock
    IdentityVerificationService identityService;

    @InjectMocks
    AccountValidator accountValidator;

    @Nested
    class ValidateRequestMethodTest {

        AccountRequestDto accountRequestDto;

        List<FieldInfoError> fields;

        @BeforeEach
        void setUp(){
            accountRequestDto = new AccountRequestDto();
            fields = new ArrayList<>();

            lenient().doAnswer(invo ->{
                FieldInfoError field = null;
                if(!invo.getArgument(0, AccountRequestDto.class).getPersonId().equals(1L)){
                    field = new FieldInfoErrorBuilder().name("registered").build();
                }
                return Optional.ofNullable(field);
            }).when(personService).validatePersonRegistered(any(AccountRequestDto.class));

            lenient().doAnswer(invo ->{
                FieldInfoError field = null;
                if(!invo.getArgument(0, AccountRequestDto.class).getPersonId().equals(1L)){
                    field = new FieldInfoErrorBuilder().name("assigned").build();
                }
                return Optional.ofNullable(field);
            }).when(accountService).validatePersonAssigned(any(AccountRequestDto.class));

            doAnswer(invo ->{
                FieldInfoError field = null;
                if(!invo.getArgument(1, AccountRequestDto.class).getEmail().equals("example@gmail.com")){
                    field = new FieldInfoErrorBuilder().name("email").build();
                }
                return Optional.ofNullable(field);
            }).when(accountService).validateUniqueEmail(isNull(), any(AccountRequestDto.class));

            doAnswer(invo ->{
                FieldInfoError field = null;
                if(!invo.getArgument(1, Boolean.class)){
                    field = new FieldInfoErrorBuilder().name("isadmin").build();
                }
                return Optional.ofNullable(field);
            }).when(identityService).validateAdminRequired(any(AccountRequestDto.class), anyBoolean());
        }

        @AfterEach
        void verifyMethodValidation(){
            verify(personService, times(1)).validatePersonRegistered(any(AccountRequestDto.class));
            verify(accountService, times(1)).validatePersonAssigned(any(AccountRequestDto.class));
            verify(accountService, times(1)).validateUniqueEmail(isNull(), any(AccountRequestDto.class));
            verify(identityService, times(1)).validateAdminRequired(any(AccountRequestDto.class), anyBoolean());
        }

        @Test
        void shouldNotThrowExceptionWhenAllConditionsAreValid() {
            accountRequestDto.setPersonId(1L);
            accountRequestDto.setEmail("example@gmail.com");
            accountRequestDto.setAdmin(true);

            assertDoesNotThrow(() -> {
                accountValidator.validateRequest(accountRequestDto);
            });
            verify(personService, times(1)).validatePersonRegistered(any(AccountRequestDto.class));
            verify(accountService, times(1)).validatePersonAssigned(any(AccountRequestDto.class));
            verify(accountService, times(1)).validateUniqueEmail(isNull(), any(AccountRequestDto.class));
            verify(identityService, times(1)).validateAdminRequired(any(AccountRequestDto.class), anyBoolean());
        }

        @Test
        void shouldThrowExceptionWith4ErrorsWhenAllConditionsAreMet(){
            accountRequestDto.setEmail("invalid@gmail.com");
            accountRequestDto.setPersonId(2L);
            accountRequestDto.setAdmin(false);
            
            fields = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> {
                    accountValidator.validateRequest(accountRequestDto);
                }).actual().getFieldErrors();

            Optional<FieldInfoError> fieldRegistered = fields.stream().filter(f -> f.getName().equals("registered")).findFirst();
            Optional<FieldInfoError> fieldAssigned = fields.stream().filter(f -> f.getName().equals("assigned")).findFirst();
            Optional<FieldInfoError> fieldEmail = fields.stream().filter(f -> f.getName().equals("email")).findFirst();
            Optional<FieldInfoError> fieldIsAdmin = fields.stream().filter(f -> f.getName().equals("isadmin")).findFirst();

            assertThat(fields).hasSize(4);

            assertThat(fieldRegistered).isNotEmpty();
            assertThat(fieldAssigned).isNotEmpty();
            assertThat(fieldEmail).isNotEmpty();
            assertThat(fieldIsAdmin).isNotEmpty();
        }

        @Test
        void shouldThrowExceptionWithOnlyPersonNotRegisteredError() {
            doReturn(Optional.of(new FieldInfoErrorBuilder().name("registered").build())).when(personService).validatePersonRegistered(any(AccountRequestDto.class));

            accountRequestDto.setEmail("example@gmail.com");
            accountRequestDto.setPersonId(1L);
            accountRequestDto.setAdmin(true);

            fields = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> {
                    accountValidator.validateRequest(accountRequestDto);
                }).actual().getFieldErrors();

            Optional<FieldInfoError> fieldRegistered = fields.stream().filter(f -> f.getName().equals("registered")).findFirst();

            assertThat(fields).hasSize(1);

            assertThat(fieldRegistered).isNotEmpty();

        }

        @Test
        void shouldThrowExceptionWithOnlyPersonAssignedError(){
            doReturn(Optional.of(new FieldInfoErrorBuilder().name("assigned").build())).when(accountService).validatePersonAssigned(any(AccountRequestDto.class));

            accountRequestDto.setEmail("example@gmail.com");
            accountRequestDto.setPersonId(1L);
            accountRequestDto.setAdmin(true);

            fields = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() ->{
                    accountValidator.validateRequest(accountRequestDto);
                }).actual().getFieldErrors();

            Optional<FieldInfoError> fieldAssigned = fields.stream().filter(f -> f.getName().equals("assigned")).findFirst();

            assertThat(fields).hasSize(1);

            assertThat(fieldAssigned).isNotEmpty();
        }

        @Test
        void shouldThrowExceptionWithOnlyUniqueEmailError(){
            accountRequestDto.setEmail("invalid@gmail.com");
            accountRequestDto.setPersonId(1L);
            accountRequestDto.setAdmin(true);

            fields = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() ->{
                    accountValidator.validateRequest(accountRequestDto);
                }).actual().getFieldErrors();

            Optional<FieldInfoError> fieldEmail = fields.stream().filter(f->f.getName().equals("email")).findFirst();

            assertThat(fields).hasSize(1);

            assertThat(fieldEmail).isNotEmpty();
        }
        
        @Test
        void shouldThrowExceptionWithOnlyAdminRequiredError(){
            accountRequestDto.setEmail("example@gmail.com");
            accountRequestDto.setPersonId(1L);
            accountRequestDto.setAdmin(false);

            fields = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() ->{
                    accountValidator.validateRequest(accountRequestDto);
                }).actual().getFieldErrors();

            Optional<FieldInfoError> fieldIsAdmin = fields.stream().filter(f->f.getName().equals("isadmin")).findFirst();

            assertThat(fields).hasSize(1);

            assertThat(fieldIsAdmin).isNotEmpty();
        }


    }

    




}
