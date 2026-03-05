package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import static com.crhistianm.springboot.gallo.springboot_gallo.workoutset.WorkoutSetData.givenWorkoutSetDtoList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

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
import org.springframework.core.env.Environment;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;


@ExtendWith(MockitoExtension.class)
class WorkoutSetValidationServiceUnitTest {

    @Mock
    private WorkoutSetRepository repository;

    @Mock
    private Environment env;

    @InjectMocks
    private WorkoutSetValidationService validationService;

    @Spy
    @InjectMocks
    private WorkoutSetValidationService spyValidationService;

    @Nested
    class ValidateWorkoutSetLimitMethodTest {

        final byte LIMIT = 10;

        WorkoutSetRequestDto requestDto; 

        Optional<FieldInfoError> expectedErrorOptional;

        @BeforeEach
        void setUp() {
            requestDto = new WorkoutSetRequestDto();
            requestDto.setWorkoutId(1L);
            requestDto.setSets(givenWorkoutSetDtoList());


            doAnswer(invo -> {
                final Long ARG_WORKOUT_ID = invo.getArgument(0, Long.class);
                return ARG_WORKOUT_ID.equals(2L);
            }).when(spyValidationService).doesWorkoutSetCountExceedLimit(anyLong(), anyByte(), anyShort());
        }

        @Test
        void shouldReturnEmptyOptional() {
            expectedErrorOptional = spyValidationService.validateWorkoutSetLimit(requestDto, LIMIT);

            assertThat(expectedErrorOptional).isEmpty();

            verify(spyValidationService, times(1)).doesWorkoutSetCountExceedLimit
                (
                 eq(requestDto.getWorkoutId()),
                 eq(LIMIT),
                 eq((short)givenWorkoutSetDtoList().size())
                );

            verifyNoInteractions(env);
        }

        @Test
        void shouldReturnErrorOptional() {
            doAnswer(invo -> {
                return invo.getArgument(0, String.class);
            }).when(env).getProperty(anyString());

            requestDto.setWorkoutId(2L);
            expectedErrorOptional = spyValidationService.validateWorkoutSetLimit(requestDto, LIMIT);

            assertThat(expectedErrorOptional).isNotEmpty();

            FieldInfoError expectedError = expectedErrorOptional.orElseThrow();

            assertThat(expectedError).extracting(FieldInfoError::getErrorMessage).isEqualTo("workoutset.validation.WorkoutSetLimit");
            assertThat(expectedError).extracting(FieldInfoError::getName).isEqualTo("sets");
            assertThat(expectedError).extracting(FieldInfoError::getOwnerClass).isEqualTo(WorkoutSetRequestDto.class);
            assertThat(expectedError).extracting(FieldInfoError::getType).isEqualTo(List.class);
            assertThat(expectedError).extracting(FieldInfoError::getValue).isEqualTo(givenWorkoutSetDtoList());

            verify(spyValidationService, times(1)).doesWorkoutSetCountExceedLimit
                (
                 eq(requestDto.getWorkoutId()),
                 eq(LIMIT),
                 eq((short)givenWorkoutSetDtoList().size())
                );
            verify(env, times(1)).getProperty(eq("workoutset.validation.WorkoutSetLimit"));
        }

    }

    @Nested
    class DoesWorkoutSetCountExceedLimitMethodTest {

        final byte LIMIT = 10;

        final byte REQUEST_SETS_COUNT = 10;

        @BeforeEach
        void setUp() {
            doAnswer(invo -> {
                short count = 0;
                Long argWorkoutId = invo.getArgument(0, Long.class);
                if(argWorkoutId.equals(2L)) count = 1;
                return count;
            }).when(repository).countByWorkoutId(anyLong());
        }

        @Test
        void shouldReturnTrueWhenLimitIsExceeded() {
            final Long WORKOUT_ID = 2L;
            boolean expectedResult = validationService.doesWorkoutSetCountExceedLimit(WORKOUT_ID, LIMIT, REQUEST_SETS_COUNT);
            assertThat(expectedResult).isTrue();

            verify(repository, times(1)).countByWorkoutId(WORKOUT_ID);
        }

        @Test
        void shouldReturnFalseWhenLimitIsNotExceeded() {
            final Long WORKOUT_ID = 1L;
            boolean expectedResult = validationService.doesWorkoutSetCountExceedLimit(WORKOUT_ID, LIMIT, REQUEST_SETS_COUNT);
            assertThat(expectedResult).isFalse();

            verify(repository, times(1)).countByWorkoutId(WORKOUT_ID);
        }

    }

}
