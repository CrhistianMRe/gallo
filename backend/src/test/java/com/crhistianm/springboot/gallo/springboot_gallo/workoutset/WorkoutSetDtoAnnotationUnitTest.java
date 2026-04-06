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

class SetRequestDtoAnnotationUnitTest {

    private Validator validator;

    SetRequestDto requestDto;

    Set<ConstraintViolation<SetRequestDto>> violations;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();

    }

    @Test
    void shouldReturnEmptyViolationsWhenDtoIsValid() {
        Integer repAmount = 10;
        Double weightAmount = 020.00;
        boolean toFailure = false;

        requestDto = new SetRequestDto(repAmount, weightAmount, toFailure);

        violations = validator.validate(requestDto, GroupsOrder.class);
        assertThat(violations).hasSize(0);
    }

    @Nested
    class RepAmountFieldTest {

        @Test
        void shouldReturnNullViolation() {
            Integer repAmount = null;
            Double weightAmount = 020.00;
            boolean toFailure = false;

            requestDto = new SetRequestDto(repAmount, weightAmount, toFailure);

            violations = validator.validate(requestDto, GroupsOrder.class);


            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be null");
            assertThat(violations).extracting(ConstraintViolation::getInvalidValue).containsNull();
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).containsOnly("repAmount");
        }

        @Test
        void shouldReturnMaxViolation() {
            Integer repAmount = 101;
            Double weightAmount = 020.00;
            boolean toFailure = false;

            requestDto = new SetRequestDto(repAmount, weightAmount, toFailure);

            violations = validator.validate(requestDto, GroupsOrder.class);

            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must be less than or equal to 100");
            assertThat(violations).extracting(ConstraintViolation::getInvalidValue).containsOnly(101);
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).containsOnly("repAmount");
        }

        @Test
        void shouldReturnMinViolation() {
            Integer repAmount = 0;
            Double weightAmount = 020.00;
            boolean toFailure = false;

            requestDto = new SetRequestDto(repAmount, weightAmount, toFailure);

            violations = validator.validate(requestDto, GroupsOrder.class);

            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must be greater than or equal to 1");
            assertThat(violations).extracting(ConstraintViolation::getInvalidValue).containsOnly(0);
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).containsOnly("repAmount");
        }

    }

}
