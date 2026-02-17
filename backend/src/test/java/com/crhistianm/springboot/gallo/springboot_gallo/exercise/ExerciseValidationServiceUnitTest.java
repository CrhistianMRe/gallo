
package com.crhistianm.springboot.gallo.springboot_gallo.exercise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
class ExerciseValidationServiceUnitTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private Environment env;

    @InjectMocks
    private ExerciseValidationService service; 

    @Spy
    @InjectMocks
    private ExerciseValidationService spyService; 

    @Nested
    class ValidateExerciseExistenceMethodTest {

        Optional<FieldInfoError> expectedFieldOptional;

        Long exerciseId;

        @BeforeEach
        void setUp() {
            exerciseId = null;
            doAnswer(invo -> invo.getArgument(0, Long.class).equals(2L)).when(spyService).exerciseExists(anyLong());
            lenient().doAnswer(invo -> invo.getArgument(0, String.class)).when(env).getProperty(anyString());
        }

        @Test
        void shouldReturnErrorOptional() {
            exerciseId = 1L;
            expectedFieldOptional = spyService.validateExerciseExistence(exerciseId);

            FieldInfoError expectedError = expectedFieldOptional.orElseThrow();

            assertThat(expectedError).extracting(FieldInfoError::getErrorMessage).isEqualTo("exercise.validation.ExerciseExistence");
            assertThat(expectedError).extracting(FieldInfoError::getValue).isEqualTo(1L);
            assertThat(expectedError).extracting(FieldInfoError::getType).isEqualTo(Long.class);
            assertThat(expectedError).extracting(FieldInfoError::getName).isEqualTo("exerciseId");

            verify(spyService, times(1)).exerciseExists(exerciseId);
        }

        @Test
        void shouldReturnEmptyOptional() {
            exerciseId = 2L;
            expectedFieldOptional = spyService.validateExerciseExistence(exerciseId);

            assertThat(expectedFieldOptional).isEmpty();

            verify(spyService, times(1)).exerciseExists(exerciseId);
        }


    }

    @Nested
    class ExerciseExistsTest {

        boolean expectedResult;

        Long exerciseId;

        @BeforeEach
        void setUp() {
            exerciseId = null;
            doAnswer(invo -> invo.getArgument(0, Long.class).equals(1L)).when(exerciseRepository).existsById(anyLong());
        }

        @Test
        void shouldReturnTrueWhenExerciseExists() {
            exerciseId = 1L;
            expectedResult = service.exerciseExists(exerciseId);

            assertThat(expectedResult).isTrue();
            verify(exerciseRepository).existsById(exerciseId);
        }

        @Test
        void shouldReturnFalseWhenExerciseDoesNotExist() {
            exerciseId = 2L;
            expectedResult = service.exerciseExists(exerciseId);

            assertThat(expectedResult).isFalse();
            verify(exerciseRepository).existsById(exerciseId);
        }

    }
    
}
