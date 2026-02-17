package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.LocalDate;
import java.util.Optional;

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
    
}
