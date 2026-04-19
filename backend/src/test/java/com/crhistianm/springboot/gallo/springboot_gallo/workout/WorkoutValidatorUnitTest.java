package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
    class ValidateByIdRequestMethodTest {

        Long accountId;

        @BeforeEach
        void setUp() {
            accountId = null;
            lenient().doAnswer(invo -> {
                FieldInfoError fieldError = null;

                final Long argAccountId = invo.getArgument(0, Long.class);

                if(!argAccountId.equals(1L)) {
                    fieldError = new FieldInfoErrorBuilder().name("allowance").build();
                }
                return Optional.ofNullable(fieldError);
            }).when(identityVerificationService).validateAllowanceByAccountId(anyLong());

            doAnswer(invo ->{
                FieldInfoError fieldError = null;

                final Long argAccountId = invo.getArgument(0, Long.class);

                if(argAccountId.equals(2L)) fieldError = new FieldInfoErrorBuilder().name("registered").build();

                return Optional.ofNullable(fieldError);
            }).when(accountService).validateAccountRegistered(anyLong());

        }

        @Test
        void shouldThrowValidationExceptionWithOnlyAccountRegisteredError() {
            accountId = 2L;

            final List<FieldInfoError> errorList = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutValidator.validateByIdRequest(accountId))
                .actual()
                .getFieldErrors();

            assertThat(errorList).hasSize(1);

            assertThat(errorList).extracting(FieldInfoError::getName).containsOnlyOnce("registered");
                
            verify(accountService, times(1)).validateAccountRegistered(eq(accountId));
            verifyNoInteractions(identityVerificationService);
        }

        @Test
        void shouldThrowValidationExceptionWithOnlyInvalidAllowanceError() {
            accountId = 3L;

            final List<FieldInfoError> errorList = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutValidator.validateByIdRequest(accountId))
                .actual()
                .getFieldErrors();

            assertThat(errorList).hasSize(1);

            assertThat(errorList).extracting(FieldInfoError::getName).containsOnlyOnce("allowance");

            verify(accountService, times(1)).validateAccountRegistered(eq(accountId));
            verify(identityVerificationService, times(1)).validateAllowanceByAccountId(eq(accountId));
        }

        @Test
        void shouldNotThrowAnyExceptionWhenRequestIsValid() {
            accountId = 1L;
            assertDoesNotThrow(() -> workoutValidator.validateByIdRequest(accountId));


            verify(accountService, times(1)).validateAccountRegistered(eq(accountId));
            verify(identityVerificationService, times(1)).validateAllowanceByAccountId(eq(accountId));
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
            doAnswer(invo -> {
                FieldInfoError error = null;
                Long id = invo.getArgument(0, Long.class);

                if(id.equals(10L)) error = new FieldInfoErrorBuilder().name("accountRegisteredError").build();

                return Optional.ofNullable(error);
            }).when(accountService).validateAccountRegistered(anyLong());

            lenient().doAnswer(invo -> {
                FieldInfoError error = null;
                Long id = invo.getArgument(0, Long.class);

                if(id.equals(2L)) error = new FieldInfoErrorBuilder().name("exerciseExistenceError").build();
                if(id.equals(10L)) error = new FieldInfoErrorBuilder().name("exerciseExistenceError").build();

                return Optional.ofNullable(error);
            }).when(exerciseService).validateExerciseExistence(anyLong());

            lenient().doAnswer(invo -> {
                FieldInfoError error = null;
                Long id = invo.getArgument(0, Long.class);

                if(id.equals(2L)) error = new FieldInfoErrorBuilder().name("allowanceByAccountIdError").build();
                if(id.equals(3L)) error = new FieldInfoErrorBuilder().name("allowanceByAccountIdError").build();
                if(id.equals(10L)) error = new FieldInfoErrorBuilder().name("allowanceByAccountIdError").build();

                return Optional.ofNullable(error);
            }).when(identityVerificationService).validateAllowanceByAccountId(anyLong());

            lenient().doAnswer(invo -> {
                FieldInfoError error = null;
                WorkoutRequestDto argRequestDto = invo.getArgument(0, WorkoutRequestDto.class);

                if(argRequestDto.getWorkoutLength() == 2) error = new FieldInfoErrorBuilder().name("perDayWorkoutLimitError").build();

                return Optional.ofNullable(error);
            }).when(workoutService).validatePerDayWorkoutLimit(any(WorkoutRequestDto.class), anyInt());
        }

        @Test
        void shouldNotThrowAnyExceptionWhenRequestIsValid() {
            Long accountId = 1L;
            LocalDate workoutDate = null;
            short workoutLength = 1;
            Long exerciseId = 1L;

            requestDto = new WorkoutRequestDto(accountId, workoutDate, workoutLength, exerciseId);

            assertDoesNotThrow(() -> workoutValidator.validateRequest(requestDto));

            verify(accountService, times(1)).validateAccountRegistered(eq(accountId));
            verify(exerciseService, times(1)).validateExerciseExistence(eq(exerciseId));
            verify(identityVerificationService, times(1)).validateAllowanceByAccountId(eq(accountId));
            verify(workoutService, times(1)).validatePerDayWorkoutLimit(eq(requestDto), eq(2));
        }

        @Test
        void shouldThrowMaximumOfTwoErrors(){
            Long accountId = 3L;
            LocalDate workoutDate = null;
            short workoutLength = 2;
            Long exerciseId = 1L;

            requestDto = new WorkoutRequestDto(accountId, workoutDate, workoutLength, exerciseId);

            list = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutValidator.validateRequest(requestDto))
                .actual()
                .getFieldErrors();

            assertThat(list).isNotEmpty();
            assertThat(list).hasSize(2);

            assertThat(list).extracting(FieldInfoError::getName).containsOnlyOnce("perDayWorkoutLimitError");
            assertThat(list).extracting(FieldInfoError::getName).containsOnlyOnce("allowanceByAccountIdError");

            verify(accountService, times(1)).validateAccountRegistered(eq(accountId));
            verify(exerciseService, times(1)).validateExerciseExistence(eq(exerciseId));
            verify(identityVerificationService, times(1)).validateAllowanceByAccountId(eq(accountId));
            verify(workoutService, times(1)).validatePerDayWorkoutLimit(eq(requestDto), eq(2));
        }

        @Test
        void shouldThrowExceptionWhenOnlyAccountIsNotRegistered() {
            Long accountId = 10L;
            LocalDate workoutDate = null;
            short workoutLength = 2;
            Long exerciseId = 10L;

            requestDto = new WorkoutRequestDto(accountId, workoutDate, workoutLength, exerciseId);

            list = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutValidator.validateRequest(requestDto))
                .actual()
                .getFieldErrors();

            assertThat(list).hasSize(1);

            assertThat(list).extracting(FieldInfoError::getName).containsOnly("accountRegisteredError");

            verify(accountService, times(1)).validateAccountRegistered(eq(accountId));
            verifyNoInteractions(exerciseService);
            verifyNoInteractions(identityVerificationService);
            verifyNoInteractions(workoutService);
        }

        @Test
        void shouldThrowExceptionOnlyExerciseDoesNotExist() {
            Long accountId = 1L;
            LocalDate workoutDate = null;
            short workoutLength = 2;
            Long exerciseId = 2L;

            requestDto = new WorkoutRequestDto(accountId, workoutDate, workoutLength, exerciseId);

            list = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutValidator.validateRequest(requestDto))
                .actual()
                .getFieldErrors();

            assertThat(list).hasSize(1);

            assertThat(list).extracting(FieldInfoError::getName).containsOnly("exerciseExistenceError");

            verify(accountService, times(1)).validateAccountRegistered(eq(accountId));
            verify(exerciseService, times(1)).validateExerciseExistence(eq(exerciseId));
            verifyNoInteractions(identityVerificationService);
            verifyNoInteractions(workoutService);
        }

        @Test
        void shouldThrowExceptionWithOnlyInvalidAllowanceError() {
            Long accountId = 3L;
            LocalDate workoutDate = null;
            short workoutLength = 1;
            Long exerciseId = 1L;

            requestDto = new WorkoutRequestDto(accountId, workoutDate, workoutLength, exerciseId);

            list = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutValidator.validateRequest(requestDto))
                .actual()
                .getFieldErrors();

            assertThat(list).isNotEmpty();
            assertThat(list).hasSize(1);

            assertThat(list).extracting(FieldInfoError::getName).containsOnly("allowanceByAccountIdError");

            verify(accountService, times(1)).validateAccountRegistered(eq(accountId));
            verify(exerciseService, times(1)).validateExerciseExistence(eq(exerciseId));
            verify(identityVerificationService, times(1)).validateAllowanceByAccountId(eq(accountId));
            verify(workoutService, times(1)).validatePerDayWorkoutLimit(eq(requestDto), eq(2));
        }

        @Test
        void shouldThrowExceptionWithOnlyPerDayWorkoutLimitError() {
            Long accountId = 1L;
            LocalDate workoutDate = null;
            short workoutLength = 2;
            Long exerciseId = 1L;

            requestDto = new WorkoutRequestDto(accountId, workoutDate, workoutLength, exerciseId);

            list = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutValidator.validateRequest(requestDto))
                .actual()
                .getFieldErrors();

            assertThat(list).isNotEmpty();
            assertThat(list).hasSize(1);

            assertThat(list).extracting(FieldInfoError::getName).containsOnly("perDayWorkoutLimitError");

            verify(accountService, times(1)).validateAccountRegistered(eq(accountId));
            verify(exerciseService, times(1)).validateExerciseExistence(eq(exerciseId));
            verify(identityVerificationService, times(1)).validateAllowanceByAccountId(eq(accountId));
            verify(workoutService, times(1)).validatePerDayWorkoutLimit(eq(requestDto), eq(2));
        }

    }

}
