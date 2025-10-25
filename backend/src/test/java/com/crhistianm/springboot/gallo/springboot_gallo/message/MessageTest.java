package com.crhistianm.springboot.gallo.springboot_gallo.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.config.MessageConfig;
import com.crhistianm.springboot.gallo.springboot_gallo.config.MessageTestConfig;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;

@ExtendWith(SpringExtension.class)
public class MessageTest {

    @Nested
    @Import(MessageTestConfig.class)
    class ValidationMessageSource {

        @Autowired
        Validator validator;

        static class TestDto {
            @NotNull(message = "{dto.validation.CorrectGender}")
            private String name;
        }

        @Test
        void shouldLoadMessageWhenFieldIsNotValid() {
            TestDto dto = new TestDto();
            assertThat(validator.validate(dto)).extracting(ConstraintViolation::getMessage).contains("is not a valid gender, use M, F or NT");
        }

    }

    @Nested
    @Import(MessageConfig.class)
    class EnvironmentTest{

        @Autowired
        Environment env;

        @Test
        void returnsMessageWhenExists() {
            assertThat(env.getProperty("person.validation.UniquePhoneNumber"))
                .isEqualTo("is already registered, user another one");
        }

        @Test
        void returnsNullMessageWhenDoesNotExist() {
            assertThat(env.getProperty("invalid.message.source"))
                .isNull();
        }

    }

    
}
