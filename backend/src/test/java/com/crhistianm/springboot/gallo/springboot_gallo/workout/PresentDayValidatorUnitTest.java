package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PresentDayValidatorUnitTest {

    private PresentDayValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PresentDayValidator();
    }

    @Test
    void shouldReturnFalseWhenPastDateIsGiven() {
        boolean expectedResponse = validator.isValid(LocalDate.now().minusDays(1), null);
        assertThat(expectedResponse).isFalse();
    }

    @Test
    void shouldReturnFalseWhenFutureDateIsGiven() {
        boolean expectedResponse = validator.isValid(LocalDate.now().plusDays(1),null);
        assertThat(expectedResponse).isFalse();
    }

    @Test
    void shouldReturnTrueWhenPresentDateIsGiven() {
        boolean expectedResponse = validator.isValid(LocalDate.now(), null);
        assertThat(expectedResponse).isTrue();
    }

}
