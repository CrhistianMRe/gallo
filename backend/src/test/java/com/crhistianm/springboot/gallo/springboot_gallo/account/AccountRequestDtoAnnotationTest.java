package com.crhistianm.springboot.gallo.springboot_gallo.account;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;



import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@SpringBootTest
class AccountRequestDtoAnnotationTest{

    @Autowired
    private Validator validator;

    @MockitoBean
    private AccountService accountService;

    Set<ConstraintViolation<AccountRequestDto>> violations;

    @Nested
    class PersonIdFieldTest{

        @Test
        void testInvalidNotNull(){
            String email = "erikuser@gmail.com";
            String password = "12345";
            Long personId = null;
            boolean admin = false;

            AccountRequestDto requestDto = new AccountRequestDto(email, password, personId, admin);

            violations = validator.validate(requestDto);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be null");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("personId");
        }
        
        @Test 
        void testValidNotNull(){
            String email = "erikuser@gmail.com";
            String password = "12345";
            Long personId = 1L;
            boolean admin = false;

            AccountRequestDto requestDto = new AccountRequestDto(email, password, personId, admin);

            violations = validator.validate(requestDto);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class EmailFieldTest{

        @Test
        void testInvalidNotBlank(){
            String email = "";
            String password = "12345";
            Long personId = 1L;
            boolean admin = false;

            AccountRequestDto requestDto = new AccountRequestDto(email, password, personId, admin);

            violations = validator.validate(requestDto);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be blank");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("email");
        }

        @Test
        void testValidNotBlank(){
            String email = "erikuser@gmail.com";
            String password = "12345";
            Long personId = 1L;
            boolean admin = false;

            AccountRequestDto requestDto = new AccountRequestDto(email, password, personId, admin);

            violations = validator.validate(requestDto);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidEmail(){
            String email = "notavalidemail,com";
            String password = "12345";
            Long personId = 1L;
            boolean admin = false;

            AccountRequestDto requestDto = new AccountRequestDto(email, password, personId, admin);

            violations = validator.validate(requestDto);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must be a well-formed email address");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("email");
        }

        @Test
        void testValidEmail(){
            String email = "erikuser@gmail.com";
            String password = "12345";
            Long personId = 1L;
            boolean admin = false;

            AccountRequestDto requestDto = new AccountRequestDto(email, password, personId, admin);

            violations = validator.validate(requestDto);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class PasswordFieldTest{

        @Test
        void testInvalidNotBlank(){

            String email = "erikuser@gmail.com";
            String password = "";
            Long personId = 1L;
            boolean admin = false;

            AccountRequestDto requestDto = new AccountRequestDto(email, password, personId, admin);

            violations = validator.validate(requestDto);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be blank");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("password");
        }

        @Test
        void testValidNotBlank(){
            String email = "erikuser@gmail.com";
            String password = "12345";
            Long personId = 1L;
            boolean admin = false;

            AccountRequestDto requestDto = new AccountRequestDto(email, password, personId, admin);

            violations = validator.validate(requestDto);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

    }


}
