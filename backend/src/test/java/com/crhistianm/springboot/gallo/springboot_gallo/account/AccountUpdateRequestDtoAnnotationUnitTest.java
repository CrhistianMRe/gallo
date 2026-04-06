package com.crhistianm.springboot.gallo.springboot_gallo.account;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.group.GroupsOrder;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class AccountUpdateRequestDtoAnnotationUnitTest{

    Validator validator;

    AccountUpdateRequestDto accountDto;

    Set<ConstraintViolation<AccountUpdateRequestDto>> violations;

    @BeforeEach
    void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Nested
    class DtoClassTest {

        @Test
        void shouldReturnEmptyRequestViolation() {
            String email = null;
            String password = null;
            Boolean enabled = null;
            List<RoleRequestDto> roles = null;
            Long personId = null;

            accountDto = new AccountUpdateRequestDto(email, password, enabled, roles, personId);

            violations = validator.validate(accountDto, GroupsOrder.class);
            String violation = violations.stream().map(ConstraintViolation::getMessage).findFirst().orElseThrow();

            assertThat(violations).hasSize(1);
            assertThat(violation).isEqualTo("{dto.validation.NotEmptyRequest}");
        }

        @Test
        void shouldNotReturnEmptyRequestViolation(){
            String email = null;
            String password = null;
            Boolean enabled = true;
            List<RoleRequestDto> roles = null;
            Long personId = null;

            accountDto = new AccountUpdateRequestDto(email, password, enabled, roles, personId);

            violations = validator.validate(accountDto, GroupsOrder.class);

            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class EmailFieldTest {

        @Test
        void shouldReturnEmailViolation(){
            String email = "bademail.com";
            String password = null;
            Boolean enabled = null;
            List<RoleRequestDto> roles = null;
            Long personId = null;

            accountDto = new AccountUpdateRequestDto(email, password, enabled, roles, personId);

            violations = validator.validate(accountDto, GroupsOrder.class);
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).contains("must be a well-formed email address");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("email");
        }

        @Test
        void shouldNotReturnEmailViolation() {
            String email = "goodemail@mail.com";
            String password = null;
            Boolean enabled = null;
            List<RoleRequestDto> roles = null;
            Long personId = null;

            accountDto = new AccountUpdateRequestDto(email, password, enabled, roles, personId);

            violations = validator.validate(accountDto, GroupsOrder.class);
            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class PasswordFieldTest {

        @Test
        void shouldReturnSizeViolation() {
            String email = null;
            String password = "222";
            Boolean enabled = null;
            List<RoleRequestDto> roles = null;
            Long personId = null;

            accountDto = new AccountUpdateRequestDto(email, password, enabled, roles, personId);

            violations = validator.validate(accountDto, GroupsOrder.class);
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("password");
        }
        
        @Test
        void shouldNotReturnSizeViolation() {
            String email = null;
            String password = "2222";
            Boolean enabled = null;
            List<RoleRequestDto> roles = null;
            Long personId = null;

            accountDto = new AccountUpdateRequestDto(email, password, enabled, roles, personId);

            violations = validator.validate(accountDto, GroupsOrder.class);
            assertThat(violations).hasSize(0);

        }

    }

    
}
