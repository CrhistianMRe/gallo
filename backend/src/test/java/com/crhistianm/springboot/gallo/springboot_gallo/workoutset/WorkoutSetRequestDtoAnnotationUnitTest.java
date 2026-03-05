package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import static com.crhistianm.springboot.gallo.springboot_gallo.workoutset.WorkoutSetData.givenWorkoutSetDtoList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Set;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.group.GroupsOrder;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class WorkoutSetRequestDtoAnnotationUnitTest {

    private Validator validator;

    WorkoutSetRequestDto workoutSetRequestDto;

    Set<ConstraintViolation<WorkoutSetRequestDto>> violations;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        workoutSetRequestDto = new WorkoutSetRequestDto();
        workoutSetRequestDto.setWorkoutId(1L); 
        workoutSetRequestDto.setSets(givenWorkoutSetDtoList());
    }

    @Test
    void shouldReturnEmptyViolationsWhenDtoIsValid() {
        violations = validator.validate(workoutSetRequestDto, GroupsOrder.class);
        assertThat(violations).isEmpty();
    }

    @Nested
    class WorkoutIdFieldTest {

        @Test
        void shouldReturnNullViolation() {
            workoutSetRequestDto.setWorkoutId(null);

            violations = validator.validate(workoutSetRequestDto, GroupsOrder.class);

            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be null");
            assertThat(violations).extracting(ConstraintViolation::getInvalidValue).containsNull();
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).containsOnly("workoutId");
        }

    }

    @Nested
    class SetsFieldTest {

        @Test
        void shouldReturnTypeEmptyViolation() {
            workoutSetRequestDto.setSets(new ArrayList<>());
            violations = validator.validate(workoutSetRequestDto, GroupsOrder.class);

            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be empty");
            assertThat(violations).extracting(ConstraintViolation::getInvalidValue)
                .first().asInstanceOf(InstanceOfAssertFactories.LIST).isEmpty();
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).containsOnly("sets");
        }

    }

}
