package com.crhistianm.springboot.gallo.springboot_gallo.validation.dto;

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

    @BeforeEach
    void setUp(){
            //To avoid message general validations until is needed
            lenient().when(personService.isPersonRegistered(anyLong())).thenReturn(true);
            lenient().when(personService.isPhoneNumberAvailable(anyString())).thenReturn(true);
            lenient().when(accountService.isEmailAvailable(anyString())).thenReturn(true);
    }

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

        @Test
        void testInvalidPersonNotAssigned(){
            when(accountService.isPersonIdAssigned(anyLong())).thenReturn(true);
            violations = validator.validate(account);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("is already assigned to an account, please use another one");
        }

        @Test
        void testValidPersonNotAssigned(){
            when(accountService.isPersonIdAssigned(anyLong())).thenReturn(false);
            violations = validator.validate(account);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidPersonRegistered(){
            when(personService.isPersonRegistered(anyLong())).thenReturn(false);
            violations = validator.validate(account);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("is not registered yet, register person first");
        }
        
        @Test
        void testValidPersonRegistered(){
            when(personService.isPersonRegistered(anyLong())).thenReturn(true);
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
        void testInvalidUniqueEmail(){
            when(accountService.isEmailAvailable(anyString())).thenReturn(false);
            violations = validator.validate(account);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("is already registered, user another one!");
        }

        @Test
        void testValidUniqueEmail(){
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

    @Nested
    class AdminFieldTest{

        @BeforeEach
        void setUp(){
            account = givenAdminAccountRequestDto().orElseThrow();
        }

        @Test
        void testInvalidAdminRequired(){
            violations = validator.validate(account);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("requires an admin user!");
        }

        @Test
        void testValidAdminRequired(){
            Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"));
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("example@gmail.com", null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            violations = validator.validate(account);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
            SecurityContextHolder.clearContext();
        }


    }

    


    
}
