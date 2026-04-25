package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import static com.crhistianm.springboot.gallo.springboot_gallo.workoutset.WorkoutSetData.givenSetRequestDtoList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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

import com.crhistianm.springboot.gallo.springboot_gallo.account.IdentityVerificationService;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;
import com.crhistianm.springboot.gallo.springboot_gallo.workout.WorkoutValidationService;


@ExtendWith(MockitoExtension.class)
class WorkoutSetValidatorUnitTest {

    @Mock
    private WorkoutValidationService workoutValidationService;

    @Mock
    private WorkoutSetValidationService setValidationService;

    @Mock
    private IdentityVerificationService identityVerificationService;

    @InjectMocks
    private WorkoutSetValidator workoutSetValidator;

    @Nested
    class ValidateSaveAllRequestMethodTest {

        List<FieldInfoError> expectedErrors; 

        WorkoutSetRequestDto requestDto;

        @BeforeEach
        void setUp() {

            doAnswer(invo ->{
                FieldInfoError responseError = null;
                final Long argWorkoutId = invo.getArgument(0, Long.class);
                if(argWorkoutId.equals(2L) || argWorkoutId.equals(4L)){
                    responseError = new FieldInfoErrorBuilder().name("workoutExistence").build();
                }
                return Optional.ofNullable(responseError);
            }).when(workoutValidationService).validateWorkoutExistence(anyLong());

            lenient().doAnswer(invo -> {
                FieldInfoError responseError = null;
                final Long argWorkoutId = invo.getArgument(0, Long.class);
                if(argWorkoutId.equals(3L) || argWorkoutId.equals(4L)) {
                    responseError = new FieldInfoErrorBuilder().name("workoutAllowance").build();
                }
                return Optional.ofNullable(responseError);
            }).when(identityVerificationService).validateUserAllowanceByWorkoutId(anyLong());

            lenient().doAnswer(invo ->{
                FieldInfoError responseError = null;
                final WorkoutSetRequestDto argRequestDto = invo.getArgument(0, WorkoutSetRequestDto.class);
                if(argRequestDto.getSets().size() > 4) responseError = new FieldInfoErrorBuilder().name("workoutSetLimit").build();
                return Optional.ofNullable(responseError);
            }).when(setValidationService).validateWorkoutSetLimit(any(WorkoutSetRequestDto.class), anyByte());
        }

        @Test
        void shouldNotThrowErrorsWhenAllConditionsAreMet() {
            Long workoutId = 1L;

            List<SetRequestDto> sets = givenSetRequestDtoList();

            requestDto = new WorkoutSetRequestDto(workoutId, sets);

            assertDoesNotThrow(() -> workoutSetValidator.validateSaveAllRequest(requestDto));

            verify(workoutValidationService, times(1)).validateWorkoutExistence(eq(requestDto.getWorkoutId()));
            verify(setValidationService, times(1)).validateWorkoutSetLimit(eq(requestDto), eq((byte)10));
            verify(identityVerificationService, times(1)).validateUserAllowanceByWorkoutId(eq(requestDto.getWorkoutId()));
        }

        @Test
        void shouldThrowMaximumOfTwoErrors() {
            Long workoutId = 3L;

            List<SetRequestDto> sets = givenSetRequestDtoList();

            Integer repAmount = null;

            Double weightAmount = null;

            boolean toFailure = false;

            sets.add(new SetRequestDto(repAmount, weightAmount, toFailure));

            requestDto = new WorkoutSetRequestDto(workoutId, sets);

            expectedErrors = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutSetValidator.validateSaveAllRequest(requestDto)).actual().getFieldErrors();

            assertThat(expectedErrors).hasSize(2);

            assertThat(expectedErrors).extracting(FieldInfoError::getName)
                .containsOnlyOnce("workoutSetLimit", "workoutAllowance");

            verify(workoutValidationService, times(1)).validateWorkoutExistence(eq(requestDto.getWorkoutId()));
            verify(setValidationService, times(1)).validateWorkoutSetLimit(eq(requestDto), eq((byte)10));
            verify(identityVerificationService, times(1)).validateUserAllowanceByWorkoutId(eq(requestDto.getWorkoutId()));
        }


        @Test
        void shouldThrowExceptionWhenOnlyLimitIsInvalid(){
            Long workoutId = 1L;

            List<SetRequestDto> sets = givenSetRequestDtoList();

            Integer repAmount = null;

            Double weightAmount = null;

            boolean toFailure = false;

            sets.add(new SetRequestDto(repAmount, weightAmount, toFailure));

            requestDto = new WorkoutSetRequestDto(workoutId, sets);

            expectedErrors = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutSetValidator.validateSaveAllRequest(requestDto)).actual().getFieldErrors();

            assertThat(expectedErrors).hasSize(1);

            assertThat(expectedErrors).extracting(FieldInfoError::getName).containsOnlyOnce("workoutSetLimit");

            verify(workoutValidationService, times(1)).validateWorkoutExistence(eq(requestDto.getWorkoutId()));
            verify(setValidationService, times(1)).validateWorkoutSetLimit(eq(requestDto), eq((byte)10));
            verify(identityVerificationService, times(1)).validateUserAllowanceByWorkoutId(eq(requestDto.getWorkoutId()));
        }

        @Test
        void shouldThrowExceptionWhenOnlyWorkoutExistenceIsInvalid(){
            Long workoutId = 2L;

            List<SetRequestDto> sets = givenSetRequestDtoList();

            Integer repAmount = null;

            Double weightAmount = null;

            boolean toFailure = false;

            sets.add(new SetRequestDto(repAmount, weightAmount, toFailure));

            requestDto = new WorkoutSetRequestDto(workoutId, sets);

            expectedErrors = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutSetValidator.validateSaveAllRequest(requestDto)).actual().getFieldErrors();

            assertThat(expectedErrors).hasSize(1);

            assertThat(expectedErrors).extracting(FieldInfoError::getName).containsOnlyOnce("workoutExistence");

            verify(workoutValidationService, times(1)).validateWorkoutExistence(eq(requestDto.getWorkoutId()));
            verifyNoMoreInteractions(setValidationService);
            verifyNoMoreInteractions(identityVerificationService);
        }

        @Test
        void shouldThrowExceptionWhenOnlyWorkoutUserAllowanceIsInvalid() {
            Long workoutId = 3L;

            List<SetRequestDto> sets = new ArrayList<>();

            Integer repAmount = null;

            Double weightAmount = null;

            boolean toFailure = false;

            sets.add(new SetRequestDto(repAmount, weightAmount, toFailure));

            requestDto = new WorkoutSetRequestDto(workoutId, sets);

            expectedErrors = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutSetValidator.validateSaveAllRequest(requestDto)).actual().getFieldErrors();

            assertThat(expectedErrors).hasSize(1);

            assertThat(expectedErrors).extracting(FieldInfoError::getName).containsOnlyOnce("workoutAllowance");

            verify(workoutValidationService, times(1)).validateWorkoutExistence(eq(requestDto.getWorkoutId()));
            verify(setValidationService, times(1)).validateWorkoutSetLimit(eq(requestDto), eq((byte)10));
            verify(identityVerificationService, times(1)).validateUserAllowanceByWorkoutId(eq(requestDto.getWorkoutId()));
        }

    }


    @Nested
    class ValidateByIdRequestMethodTest {

        List<FieldInfoError> expectedErrors;

        Long workoutdId;

        @BeforeEach
        void setUp() {
            doAnswer(invo ->{
                FieldInfoError responseError = null;
                final Long argWorkoutId = invo.getArgument(0, Long.class);
                if(argWorkoutId.equals(4L)){
                    responseError = new FieldInfoErrorBuilder().name("workoutExistence").build();
                }
                return Optional.ofNullable(responseError);
            }).when(workoutValidationService).validateWorkoutExistence(anyLong());

            lenient().doAnswer(invo -> {
                FieldInfoError responseError = null;
                final Long argWorkoutId = invo.getArgument(0, Long.class);
                if(argWorkoutId.equals(3L) || argWorkoutId.equals(4L)) {
                    responseError = new FieldInfoErrorBuilder().name("workoutAllowance").build();
                }
                return Optional.ofNullable(responseError);
            }).when(identityVerificationService).validateUserAllowanceByWorkoutId(anyLong());
        }

        @Test
        void shouldThrowExceptionWhenOnlyWorkoutExistenceIsInvalid(){
            Long workoutId = 4L;

            expectedErrors = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutSetValidator.validateByIdRequest(workoutId))
                .actual()
                .getFieldErrors();

            assertThat(expectedErrors).hasSize(1);

            assertThat(expectedErrors).extracting(FieldInfoError::getName).containsOnlyOnce("workoutExistence");

            verify(workoutValidationService, times(1)).validateWorkoutExistence(eq(workoutId));
            verifyNoMoreInteractions(identityVerificationService);
        }

        @Test
        void shouldThrowExceptionWhenOnlyWorkoutUserAllowanceIsInvalid() {
            Long workoutId = 3L;

            expectedErrors = assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> workoutSetValidator.validateByIdRequest(workoutId))
                .actual()
                .getFieldErrors();

            assertThat(expectedErrors).hasSize(1);

            assertThat(expectedErrors).extracting(FieldInfoError::getName).containsOnlyOnce("workoutAllowance");

            verify(workoutValidationService, times(1)).validateWorkoutExistence(eq(workoutId));
            verify(identityVerificationService, times(1)).validateUserAllowanceByWorkoutId(eq(workoutId));
        }

    }
    
}
