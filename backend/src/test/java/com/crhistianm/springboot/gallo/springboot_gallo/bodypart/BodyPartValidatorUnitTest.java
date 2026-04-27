package com.crhistianm.springboot.gallo.springboot_gallo.bodypart;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.exercise.ExerciseValidationService;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;

@ExtendWith(MockitoExtension.class)
class BodyPartValidatorUnitTest {

    @Mock
    private ExerciseValidationService exerciseValidationService;

    @InjectMocks
    private BodyPartValidator bodyPartValidator;

    @Nested
    class ValidateByIdRequestTest {

        Long exerciseId;

        @BeforeEach
        void setUp() {
            doAnswer(invo -> {
                FieldInfoError fieldError = null;

                final Long argExerciseId = invo.getArgument(0, Long.class);

                if(argExerciseId.equals(99L)) fieldError = new FieldInfoError();
                
                return Optional.ofNullable(fieldError);
            }).when(exerciseValidationService).validateExerciseExistence(anyLong());

        }

        @Test
        void shouldThrowExceptionWhenExerciseExistenceIsInvalid() {
            exerciseId = 99L;

            assertThatExceptionOfType(ValidationServiceException.class)
                .isThrownBy(() -> bodyPartValidator.validateByIdRequest(exerciseId));

            verify(exerciseValidationService, times(1)).validateExerciseExistence(eq(exerciseId));
        }

        @Test
        void shouldNotThrowAnyException() {
            exerciseId = 1L;

            assertThatCode(() -> bodyPartValidator.validateByIdRequest(exerciseId))
                .doesNotThrowAnyException();

            verify(exerciseValidationService, times(1)).validateExerciseExistence(eq(exerciseId));
        }

    }
   
}
