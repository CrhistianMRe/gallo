package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.account.IdentityVerificationService;

@ExtendWith(MockitoExtension.class)
class WorkoutValidatorUnitTest {

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
                FieldInfoError errorOptional = null;
                if(invo.getArgument(0, Long.class).equals(2L)) {
                    errorOptional = new FieldInfoErrorBuilder().name("error").build();
                }
                return Optional.ofNullable(errorOptional);
            }).when(identityVerificationService).validateAllowanceByAccountId(anyLong());

        }

        @Test
        void shouldThrowValidationExceptionWhenUserIsNotAllowed() {
            accountId = 2L;

            String errorName = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutValidator.validateByIdRequest(accountId))
                .actual().getFieldErrors().get(0).getName();

            assertThat(errorName).isEqualTo("error");

            verify(identityVerificationService).validateAllowanceByAccountId(accountId);
        }

        @Test
        void shouldNotThrowAnyExceptionWhenRequestIsValid() {
            accountId = 1L;
            assertDoesNotThrow(() -> workoutValidator.validateByIdRequest(accountId));

            verify(identityVerificationService).validateAllowanceByAccountId(accountId);
        }

    }
    
}
