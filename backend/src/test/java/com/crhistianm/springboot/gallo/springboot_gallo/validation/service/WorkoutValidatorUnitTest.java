package com.crhistianm.springboot.gallo.springboot_gallo.validation.service;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenAccountEntityAdmin;
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenAccountEntityUser;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.FieldInfoErrorBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;
import com.crhistianm.springboot.gallo.springboot_gallo.service.IdentityVerificationService;

@ExtendWith(MockitoExtension.class)
public class WorkoutValidatorUnitTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private IdentityVerificationService identityVerificationService;

    @InjectMocks
    private WorkoutValidator workoutValidator;

    @Nested
    class ViewModule {

        Long accountId;

        @BeforeEach
        void setUp() {
            accountId = null;

            doAnswer(invo -> {
                Account account = null;
                Long argId = invo.getArgument(0, Long.class);
                if(argId.equals(1L)) {
                    account = givenAccountEntityAdmin().orElseThrow();
                    account.getPerson().setId(1L);
                }
                if(argId.equals(2L)) {
                    account = givenAccountEntityUser().orElseThrow();
                    account.getPerson().setId(2L);
                }
                return Optional.ofNullable(account);
            }).when(accountRepository).findById(anyLong());

            lenient().doAnswer(invo -> {
                FieldInfoError errorOptional = null;
                if(invo.getArgument(0, Long.class).equals(2L)) {
                    errorOptional = new FieldInfoErrorBuilder().name("error").build();
                }
                return Optional.ofNullable(errorOptional);
            }).when(identityVerificationService).validateUserAllowance(anyLong());

        }

        @Test
        void shouldThrowExceptionWhenAccountIsNotFound() {
            accountId = 3L;

            String error = assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> workoutValidator.validateByIdRequest(accountId)).actual().getMessage();

            assertThat(error).isEqualTo("Account not found");

            verify(accountRepository, times(1)).findById(accountId);
            verifyNoInteractions(identityVerificationService);
        }


        @Test
        void shouldThrowValidationExceptionWhenUserIsNotAllowed() {
            accountId = 2L;

            String errorName = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutValidator.validateByIdRequest(accountId))
                .actual().getFieldErrors().get(0).getName();

            assertThat(errorName).isEqualTo("error");

            verify(accountRepository, times(1)).findById(accountId);
            verify(identityVerificationService).validateUserAllowance(eq(2L));
        }

        @Test
        void shouldNotThrowAnyExceptionWhenRequestIsValid() {
            accountId = 1L;
            assertDoesNotThrow(() -> workoutValidator.validateByIdRequest(accountId));

            verify(accountRepository, times(1)).findById(accountId);
            verify(identityVerificationService).validateUserAllowance(eq(1L));
        }

    }
    
}
