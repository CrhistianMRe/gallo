package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
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
import com.crhistianm.springboot.gallo.springboot_gallo.account.AccountValidationService;
import com.crhistianm.springboot.gallo.springboot_gallo.account.IdentityVerificationService;
import com.crhistianm.springboot.gallo.springboot_gallo.exercise.ExerciseValidationService;

@ExtendWith(MockitoExtension.class)
class WorkoutValidatorUnitTest {

    @Mock
    private IdentityVerificationService identityVerificationService;

    @Mock
    private AccountValidationService accountService;

    @Mock
    private ExerciseValidationService exerciseService;

    @Mock
    private WorkoutValidationService workoutService;

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


    @Nested
    class ValidateRequestMethodTest {

        List<FieldInfoError> list;

        WorkoutRequestDto requestDto;

        int testLimit;

        @BeforeEach
        void setUp() {
            list = new ArrayList<>();
            requestDto = new WorkoutRequestDto();

            doAnswer(invo -> {
                FieldInfoError error = null;
                Long id = invo.getArgument(0, Long.class);

                if(id.equals(2L)) error = new FieldInfoErrorBuilder().name("accountRegisteredError").build();
                if(id.equals(10L)) error = new FieldInfoErrorBuilder().name("accountRegisteredError").build();

                return Optional.ofNullable(error);
            }).when(accountService).validateAccountRegistered(anyLong());

            doAnswer(invo -> {
                FieldInfoError error = null;
                Long id = invo.getArgument(0, Long.class);

                if(id.equals(2L)) error = new FieldInfoErrorBuilder().name("exerciseExistenceError").build();

                return Optional.ofNullable(error);
            }).when(exerciseService).validateExerciseExistence(anyLong());

            doAnswer(invo -> {
                FieldInfoError error = null;
                Long id = invo.getArgument(0, Long.class);

                if(id.equals(3L)) error = new FieldInfoErrorBuilder().name("allowanceByAccountIdError").build();
                if(id.equals(10L)) error = new FieldInfoErrorBuilder().name("allowanceByAccountIdError").build();

                return Optional.ofNullable(error);
            }).when(identityVerificationService).validateAllowanceByAccountId(anyLong());

            doAnswer(invo -> {
                FieldInfoError error = null;
                WorkoutRequestDto argRequestDto = invo.getArgument(0, WorkoutRequestDto.class);

                if(argRequestDto.getWorkoutLength() == 2) error = new FieldInfoErrorBuilder().name("perDayWorkoutLimitError").build();

                return Optional.ofNullable(error);
            }).when(workoutService).validatePerDayWorkoutLimit(any(WorkoutRequestDto.class), anyInt());
        }

        @AfterEach
        void setUpAfterEach() {
            verify(workoutService, times(1)).validatePerDayWorkoutLimit(eq(requestDto), eq(2));
            verify(accountService, times(1)).validateAccountRegistered(requestDto.getAccountId());
            verify(exerciseService, times(1)).validateExerciseExistence(requestDto.getExerciseId());
            verify(identityVerificationService, times(1)).validateAllowanceByAccountId(requestDto.getAccountId());
        }

        @Test
        void shouldNotThrowAnyExceptionWhenRequestIsValid() {
            requestDto.setAccountId(1L);
            requestDto.setWorkoutLength((short)1);
            requestDto.setExerciseId(1L);

            assertDoesNotThrow(() -> workoutValidator.validateRequest(requestDto));
        }

        @Test
        void shouldThrowMaximumAmountOfErrorsWhenAllFieldsAreInvalid(){
            requestDto.setAccountId(10L);
            requestDto.setWorkoutLength((short)2);
            requestDto.setExerciseId(2L);

            list = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutValidator.validateRequest(requestDto))
                .actual()
                .getFieldErrors();

            assertThat(list).isNotEmpty();
            assertThat(list).hasSize(4);

            assertThat(list).extracting(FieldInfoError::getName).contains("exerciseExistenceError");
            assertThat(list).extracting(FieldInfoError::getName).contains("perDayWorkoutLimitError");
            assertThat(list).extracting(FieldInfoError::getName).contains("accountRegisteredError");
            assertThat(list).extracting(FieldInfoError::getName).contains("allowanceByAccountIdError");
        }

        @Test
        void shouldThrowExceptionWhenAccountIsNotRegistered() {
            requestDto.setAccountId(2L);
            requestDto.setWorkoutLength((short)1);
            requestDto.setExerciseId(1L);

            list = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutValidator.validateRequest(requestDto))
                .actual()
                .getFieldErrors();

            assertThat(list).isNotEmpty();
            assertThat(list).hasSize(1);

            assertThat(list).extracting(FieldInfoError::getName).containsOnly("accountRegisteredError");
        }

        @Test
        void shouldThrowExceptionWhenExerciseDoesNotExist() {
            requestDto.setAccountId(1L);
            requestDto.setWorkoutLength((short)1);
            requestDto.setExerciseId(2L);

            list = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutValidator.validateRequest(requestDto))
                .actual()
                .getFieldErrors();

            assertThat(list).isNotEmpty();
            assertThat(list).hasSize(1);

            assertThat(list).extracting(FieldInfoError::getName).containsOnly("exerciseExistenceError");
        }

        @Test
        void shouldThrowExceptionWhenUserIsNotAllowed() {
            requestDto.setAccountId(3L);
            requestDto.setWorkoutLength((short)1);
            requestDto.setExerciseId(1L);

            list = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutValidator.validateRequest(requestDto))
                .actual()
                .getFieldErrors();

            assertThat(list).isNotEmpty();
            assertThat(list).hasSize(1);

            assertThat(list).extracting(FieldInfoError::getName).containsOnly("allowanceByAccountIdError");
        }

        @Test
        void shouldThrowExceptionWhenPerDayWorkoutLimitIsExceeded() {
            requestDto.setAccountId(1L);
            requestDto.setWorkoutLength((short)2);
            requestDto.setExerciseId(1L);

            list = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutValidator.validateRequest(requestDto))
                .actual()
                .getFieldErrors();

            assertThat(list).isNotEmpty();
            assertThat(list).hasSize(1);

            assertThat(list).extracting(FieldInfoError::getName).containsOnly("perDayWorkoutLimitError");
        }

    }

}
