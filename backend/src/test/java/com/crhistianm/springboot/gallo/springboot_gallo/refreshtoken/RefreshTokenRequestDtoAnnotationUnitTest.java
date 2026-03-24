package com.crhistianm.springboot.gallo.springboot_gallo.refreshtoken;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class RefreshTokenRequestDtoAnnotationUnitTest {

    private Validator validator;

    private Set<ConstraintViolation<RefreshTokenRequestDto>> violations;

    private RefreshTokenRequestDto requestDto;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldNotReturnAnyViolation() {
        final String refreshToken = "anything";

        requestDto = new RefreshTokenRequestDto(refreshToken);

        violations = validator.validate(requestDto);

        assertThat(violations).isEmpty();
    }

    @Nested
    class RefreshTokenField {

        @Test
        void shouldReturnNotNullViolation() {
            final String refreshToken = null;

            requestDto = new RefreshTokenRequestDto(refreshToken);

            violations = validator.validate(requestDto);

            assertThat(violations).isNotEmpty();

            assertThat(violations).hasSize(1);

            assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("must not be null");

            assertThat(violations).extracting(c -> c.getPropertyPath().toString())
                .contains("refreshToken");
        }

    }

}
