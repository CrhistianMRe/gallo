package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.text.NumberFormat.Field;
import java.time.LocalDate;
import java.util.Optional;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;

@ExtendWith(MockitoExtension.class)
class WorkoutValidationServiceUnitTest {

    @Mock
    private Environment env;

    @Mock
    private WorkoutRepository workoutRepository;

    @InjectMocks
    private WorkoutValidationService service;

    @Spy
    @InjectMocks
    private WorkoutValidationService spyService;

    @Nested
    class DoesWorkoutCountExceedLimitMethodTest {

        boolean result;

        final int LIMIT = 2;

        Long accountId;

        final Long EXERCISE_ID = 1L;

        final LocalDate DATE = LocalDate.of(1, 1, 1);

        @BeforeEach
        void setUp() {
            accountId = null;
            result = false;
            doAnswer(invo -> {
                int count = 0;
                Long argAccountId = invo.getArgument(0, Long.class);
                if(argAccountId.equals(1L)) count = 1;
                if(argAccountId.equals(2L)) count = 2;
                if(argAccountId.equals(3L)) count = 3;
                return count;
            }).when(workoutRepository).countPerDayByAccountAndExercise(anyLong(), any(LocalDate.class), anyLong());
        }

        @Test
        void shouldReturnTrueWhenLimitIsExceeded() {
            accountId = 3L;

            result = service.doesWorkoutCountExceedLimit(accountId, DATE, EXERCISE_ID, LIMIT);
            assertThat(result).isTrue();
            verify(workoutRepository, times(1)).countPerDayByAccountAndExercise(accountId, DATE, EXERCISE_ID);
        }

        @Test
        void shouldReturnFalseWhenLimitIsNotExceeded() {
            accountId = 1L;

            result = service.doesWorkoutCountExceedLimit(accountId, DATE, EXERCISE_ID, LIMIT);
            assertThat(result).isFalse();
            verify(workoutRepository, times(1)).countPerDayByAccountAndExercise(accountId, DATE, EXERCISE_ID);
        }

        @Test
        void shouldReturnTrueWhenCountIsSameAsLimit() {
            accountId = 2L;

            result = service.doesWorkoutCountExceedLimit(accountId, DATE, EXERCISE_ID, LIMIT);
            assertThat(result).isTrue();
            verify(workoutRepository, times(1)).countPerDayByAccountAndExercise(accountId, DATE, EXERCISE_ID);
        }

    }

    @Nested
    class ValidatePerDayWorkoutLimitMethodTest {

        Optional<FieldInfoError> expectedErrorOptional;

        WorkoutRequestDto requestDto;

        @BeforeEach
        void setUp() {
            requestDto = new WorkoutRequestDto();
            requestDto.setAccountId(1L);
            requestDto.setExerciseId(1L);
            requestDto.setWorkoutDate(LocalDate.of(1, 1, 1));
            expectedErrorOptional = Optional.empty(); 
            doAnswer(invo -> {
                return invo.getArgument(3, Integer.class) == 1;
            }).when(spyService).doesWorkoutCountExceedLimit(anyLong(), any(LocalDate.class), anyLong(), anyInt());

            lenient().doAnswer(invo -> invo.getArgument(0, String.class)).when(env).getProperty(anyString());
        }

        @Test
        void shouldReturnErrorOptional() {
            final int LIMIT = 1;
            expectedErrorOptional = spyService.validatePerDayWorkoutLimit(requestDto, LIMIT);

            assertThat(expectedErrorOptional).isNotEmpty();

            FieldInfoError expectedError = expectedErrorOptional.orElseThrow();

            assertThat(expectedError).extracting(FieldInfoError::getErrorMessage).isEqualTo("workout.validation.PerDayWorkoutLimit");
            assertThat(expectedError).extracting(FieldInfoError::getValue).isEqualTo(LocalDate.of(1, 1, 1));
            assertThat(expectedError).extracting(FieldInfoError::getType).isEqualTo(LocalDate.class);
            assertThat(expectedError).extracting(FieldInfoError::getName).isEqualTo("workoutDate");
            assertThat(expectedError).extracting(FieldInfoError::getOwnerClass).isEqualTo(WorkoutRequestDto.class);

            verify(spyService, times(1)).doesWorkoutCountExceedLimit(requestDto.getAccountId(), requestDto.getWorkoutDate(), requestDto.getExerciseId(), LIMIT);
        }


        @Test
        void shouldReturnEmptyOptional() {
            final int LIMIT = 2;
            expectedErrorOptional = spyService.validatePerDayWorkoutLimit(requestDto, LIMIT);
            assertThat(expectedErrorOptional).isEmpty();

            verify(spyService, times(1)).doesWorkoutCountExceedLimit(requestDto.getAccountId(), requestDto.getWorkoutDate(), requestDto.getExerciseId(), LIMIT);
        }

    }

    @Nested
    class WorkoutExistsMethodTest {

        @BeforeEach
        void setUp() {
            doAnswer(invo -> {
                Long argWorkoutId = invo.getArgument(0, Long.class);
                return argWorkoutId.equals(1L);
            }).when(workoutRepository).existsById(anyLong());
        }

        @Test
        void shouldReturnTrueWhenWorkoutExists() {
            final Long WORKOUT_ID = 1L;
            boolean expectedResult = service.workoutExists(WORKOUT_ID);

            assertThat(expectedResult).isTrue();

            verify(workoutRepository, times(1)).existsById(eq(WORKOUT_ID));
        }

        @Test
        void shouldReturnFalseWhenWorkoutDoesNotExist() {
            final Long WORKOUT_ID = 2L;
            boolean expectedResult = service.workoutExists(WORKOUT_ID);

            assertThat(expectedResult).isFalse();

            verify(workoutRepository, times(1)).existsById(eq(WORKOUT_ID));
        }

    }

    @Nested
    class ValidateWorkoutExistenceMethodTest {

        Optional<FieldInfoError> expectedErrorOptional;

        @BeforeEach
        void setUp() {
            doAnswer(invo -> {
                Long argWorkoutId = invo.getArgument(0, Long.class);
                return argWorkoutId.equals(1L);
            }).when(spyService).workoutExists(anyLong());
        }

        @Test
        void shouldReturnEmptyOptionalWhenWorkoutDoesNotExist() {
            final Long WORKOUT_ID = 2L;

            expectedErrorOptional = spyService.validateWorkoutExistence(WORKOUT_ID);

            assertThat(expectedErrorOptional).isEmpty();

            verify(spyService, times(1)).workoutExists(eq(WORKOUT_ID));
            verifyNoInteractions(env);
        }

        @Test
        void shouldReturnErrorOptionalWhenWorkoutExists() {
            doReturn("Environment").when(env).getProperty(anyString());
            final Long WORKOUT_ID = 1L;

            expectedErrorOptional = spyService.validateWorkoutExistence(WORKOUT_ID);

            assertThat(expectedErrorOptional).isNotEmpty();

            FieldInfoError expectedError = expectedErrorOptional.orElseThrow();

            assertThat(expectedError).extracting(FieldInfoError::getName).isEqualTo("workoutId");
            assertThat(expectedError).extracting(FieldInfoError::getErrorMessage).isEqualTo("Environment");
            assertThat(expectedError).extracting(FieldInfoError::getValue).isEqualTo(WORKOUT_ID);
            assertThat(expectedError).extracting(FieldInfoError::getType).isEqualTo(Long.class);

            verify(spyService, times(1)).workoutExists(eq(WORKOUT_ID));
            verify(env, times(1)).getProperty(eq("workout.validation.WorkoutExistence"));
        }

    }
    
}
