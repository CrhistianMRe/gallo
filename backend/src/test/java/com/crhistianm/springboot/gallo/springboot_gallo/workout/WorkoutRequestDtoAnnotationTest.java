package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.config.ValidatorConfig;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.group.GroupsOrder;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@ExtendWith(SpringExtension.class)
@Import(ValidatorConfig.class)
class WorkoutRequestDtoAnnotationTest {

    @Autowired
    Validator validator;

    WorkoutRequestDto requestDto;

    Set<ConstraintViolation<WorkoutRequestDto>> violations;

    @Test
    void shouldReturnEmptyViolationsWhenDtoIsValid() {

        Long accountId = 1L;
        LocalDate workoutDate = LocalDate.now();
        short workoutLength = 60;
        Long exerciseId = 1L;

        requestDto = new WorkoutRequestDto(accountId, workoutDate, workoutLength, exerciseId);

        violations = validator.validate(requestDto, GroupsOrder.class);
        assertThat(violations).isEmpty();
    }

    @Nested
    class WorkoutDateFieldTest {

        @Test
        void shouldReturnNullViolation() {
            Long accountId = 1L;
            LocalDate workoutDate = null;
            short workoutLength = 60;
            Long exerciseId = 1L;

            requestDto = new WorkoutRequestDto(accountId, workoutDate, workoutLength, exerciseId);

            violations = validator.validate(requestDto, GroupsOrder.class);

            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).contains("must not be null");
            assertThat(violations).extracting(ConstraintViolation::getInvalidValue).containsNull();
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("workoutDate");
        }

        @Test
        void shouldReturnPresentDayViolation() {
            Long accountId = 1L;
            LocalDate workoutDate = LocalDate.of(1,1,1);
            short workoutLength = 60;
            Long exerciseId = 1L;

            requestDto = new WorkoutRequestDto(accountId, workoutDate, workoutLength, exerciseId);

            violations = validator.validate(requestDto, GroupsOrder.class);

            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).contains("{dto.validation.PresentDay}");
            assertThat(violations).extracting(ConstraintViolation::getInvalidValue).contains(requestDto.getWorkoutDate());
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("workoutDate");
        }

    }


    @Nested
    class WorkoutLengthFieldTest {

        @Test
        void shouldReturnMinimumValueViolation() {
            Long accountId = 1L;
            LocalDate workoutDate = LocalDate.now();
            short workoutLength = 19;
            Long exerciseId = 1L;

            requestDto = new WorkoutRequestDto(accountId, workoutDate, workoutLength, exerciseId);

            violations = validator.validate(requestDto, GroupsOrder.class);

            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).contains("must be greater than or equal to 20");
            assertThat(violations).extracting(ConstraintViolation::getInvalidValue).contains((short)19);
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("workoutLength");
        }

        @Test
        void shouldReturnMaximumValueViolation() {
            Long accountId = 1L;
            LocalDate workoutDate = LocalDate.now();
            short workoutLength = Short.MAX_VALUE;
            Long exerciseId = 1L;

            requestDto = new WorkoutRequestDto(accountId, workoutDate, workoutLength, exerciseId);

            violations = validator.validate(requestDto, GroupsOrder.class);

            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).contains("must be less than or equal to 32766");
            assertThat(violations).extracting(ConstraintViolation::getInvalidValue).contains(Short.MAX_VALUE);
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("workoutLength");
        }

    }

    @Nested
    class AccountIdFieldTest {

        @Test
        void shouldReturnNullViolation() {
            Long accountId = null;
            LocalDate workoutDate = LocalDate.now();
            short workoutLength = Short.MAX_VALUE;
            Long exerciseId = 1L;

            requestDto = new WorkoutRequestDto(accountId, workoutDate, workoutLength, exerciseId);

            violations = validator.validate(requestDto, GroupsOrder.class);

            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).contains("must not be null");
            assertThat(violations).extracting(ConstraintViolation::getInvalidValue).containsNull();
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("accountId");
        }

    }

    @Nested
    class ExerciseIdFieldTest {

        @Test
        void shouldReturnNullViolation() {
            Long accountId = 1L;
            LocalDate workoutDate = LocalDate.now();
            short workoutLength = Short.MAX_VALUE;
            Long exerciseId = null;

            requestDto = new WorkoutRequestDto(accountId, workoutDate, workoutLength, exerciseId);

            violations = validator.validate(requestDto, GroupsOrder.class);

            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).contains("must not be null");
            assertThat(violations).extracting(ConstraintViolation::getInvalidValue).containsNull();
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("exerciseId");
        }

    }

}
