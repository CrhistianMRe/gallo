package com.crhistianm.springboot.gallo.springboot_gallo.validation.annotation.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountUpdateRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.validation.annotation.group.GroupsOrder;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class AccountUpdateRequestDtoAnnotationUnitTest{

    Validator validator;

    AccountUpdateRequestDto accountDto;

    Set<ConstraintViolation<AccountUpdateRequestDto>> violations;

    @BeforeEach
    void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        accountDto = new AccountUpdateRequestDto();
    }

    @Nested
    class DtoClassTest {

        @Test
        void shouldReturnEmptyRequestViolation() {
            violations = validator.validate(accountDto, GroupsOrder.class);
            String violation = violations.stream().map(ConstraintViolation::getMessage).findFirst().orElseThrow();

            assertThat(violations).hasSize(1);
            assertThat(violation).isEqualTo("{dto.validation.NotEmptyRequest}");
        }

        @Test
        void shouldNotReturnEmptyRequestViolation(){
            accountDto.setEnabled(true);
            violations = validator.validate(accountDto, GroupsOrder.class);

            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class EmailFieldTest {

        @Test
        void shouldReturnEmailViolation(){
            accountDto.setEmail("bademail.com");
            violations = validator.validate(accountDto, GroupsOrder.class);
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).contains("must be a well-formed email address");
        }

        @Test
        void shouldNotReturnEmailViolation() {
            accountDto.setEmail("goodemail@mail.com");
            violations = validator.validate(accountDto, GroupsOrder.class);
            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class PasswordFieldTest {

        @Test
        void shouldReturnSizeViolation() {
            accountDto.setPassword("222");
            violations = validator.validate(accountDto, GroupsOrder.class);
            assertThat(violations).hasSize(1);

        }
        
        @Test
        void shouldNotReturnSizeViolation() {
            accountDto.setPassword("2222");
            violations = validator.validate(accountDto, GroupsOrder.class);
            assertThat(violations).hasSize(0);

        }

    }

    
}
