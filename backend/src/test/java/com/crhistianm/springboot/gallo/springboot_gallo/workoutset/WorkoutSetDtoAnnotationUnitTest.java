package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.group.GroupsOrder;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class WorkoutSetDtoAnnotationUnitTest {

    private Validator validator;

    WorkoutSetDto workoutSetDto;

    Set<ConstraintViolation<WorkoutSetDto>> violations;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        workoutSetDto = new WorkoutSetDto();
        workoutSetDto.setRepAmount(10);
        workoutSetDto.setWeightAmount(020.00);
    }

    @Test
    void shouldReturnEmptyViolationsWhenDtoIsValid() {
        violations = validator.validate(workoutSetDto, GroupsOrder.class);
        assertThat(violations).hasSize(0);
    }

    @Nested
    class RepAmountFieldTest {

        @Test
        void shouldReturnNullViolation() {
            workoutSetDto.setRepAmount(null);
            violations = validator.validate(workoutSetDto, GroupsOrder.class);


            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be null");
            assertThat(violations).extracting(ConstraintViolation::getInvalidValue).containsNull();
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).containsOnly("repAmount");
        }

        @Test
        void shouldReturnMaxViolation() {
            workoutSetDto.setRepAmount(101);
            violations = validator.validate(workoutSetDto, GroupsOrder.class);

            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must be less than or equal to 100");
            assertThat(violations).extracting(ConstraintViolation::getInvalidValue).containsOnly(101);
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).containsOnly("repAmount");
        }

        @Test
        void shouldReturnMinViolation() {
            workoutSetDto.setRepAmount(0);
            violations = validator.validate(workoutSetDto, GroupsOrder.class);

            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must be greater than or equal to 1");
            assertThat(violations).extracting(ConstraintViolation::getInvalidValue).containsOnly(0);
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).containsOnly("repAmount");
        }

    }

}
