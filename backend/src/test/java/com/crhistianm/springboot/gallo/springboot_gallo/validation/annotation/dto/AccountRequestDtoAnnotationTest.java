package com.crhistianm.springboot.gallo.springboot_gallo.validation.annotation.dto;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenAdminAccountRequestDto;
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenUserAccountRequestDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.service.AccountServiceImpl;
import com.crhistianm.springboot.gallo.springboot_gallo.service.PersonServiceImpl;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@SpringBootTest
public class AccountRequestDtoAnnotationTest{

    @Autowired
    private Validator validator;

    private AccountRequestDto account;

    @MockitoBean
    private PersonServiceImpl personService;

    @MockitoBean
    private AccountServiceImpl accountService;

    Set<ConstraintViolation<AccountRequestDto>> violations;


    @Nested
    class PersonIdFieldTest{

        @BeforeEach
        void setUp(){
            account = givenUserAccountRequestDto().orElseThrow();
        }

        @Test
        void testInvalidNotNull(){
            account.setPersonId(null);
            violations = validator.validate(account);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be null");
        }
        
        @Test 
        void testValidNotNull(){
            violations = validator.validate(account);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class EmailFieldTest{
        
        @BeforeEach
        void setUp(){
            account = givenUserAccountRequestDto().orElseThrow();
        }

        @Test
        void testInvalidNotBlank(){
            account.setEmail("");
            violations = validator.validate(account);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be blank");
        }

        @Test
        void testValidNotBlank(){
            violations = validator.validate(account);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidEmail(){
            account.setEmail("notavalidemail,com");
            violations = validator.validate(account);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must be a well-formed email address");
        }

        @Test
        void testValidEmail(){
            violations = validator.validate(account);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class PasswordFieldTest{

        @BeforeEach
        void setUp(){
            account = givenUserAccountRequestDto().orElseThrow();
        }

        @Test
        void testInvalidNotBlank(){
            account.setPassword("");
            violations = validator.validate(account);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be blank");
        }

        @Test
        void testValidNotBlank(){
            violations = validator.validate(account);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

    }


}
