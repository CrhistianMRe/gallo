package com.crhistianm.springboot.gallo.springboot_gallo.validation.annotation.dto;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenPersonRequestDtoOne;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.service.PersonServiceImpl;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class PersonRequestDtoAnnotationTest {

    private Validator validator;

    private PersonRequestDto person;

    Set<ConstraintViolation<PersonRequestDto>> violations;

    @MockitoBean
    private PersonServiceImpl personService;

    @BeforeEach
    void setUp(){
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Nested
    class FirstNameFieldTest{
        
        @BeforeEach
        void setUp(){
            person = givenPersonRequestDtoOne().orElseThrow();
        }

        @Test
        void testInvalidNotBlank(){
            person.setFirstName("");
            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be blank");
        }

        @Test
        void testValidNotBlank(){
            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidSize(){
            person.setFirstName("somenametoolongsomenametoolongsomenametoolongsomenametoolong");
            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("size must be between 0 and 45");
        }

        @Test
        void testValidSize(){
            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class LastNameFieldTest{

        @BeforeEach
        void setUp(){
            person = givenPersonRequestDtoOne().orElseThrow();
        }

        @Test
        void testInvalidNotBlank(){
            person.setLastName("");
            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be blank");
        }

        @Test
        void testValidNotBlank(){
            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidSize(){
            person.setLastName("somelastnametoolongsomelastnametoolongsomelastnametoolongsomelastnametoolong");
            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("size must be between 0 and 45");
        }

        @Test
        void testValidSize(){
            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class PhoneNumberFieldTest{
        
        @BeforeEach
        void setUp(){
            person = givenPersonRequestDtoOne().orElseThrow();
        }

        @Test
        void testInvalidNotBlank(){
            person.setPhoneNumber("");
            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be blank");
        }

        @Test
        void testValidNotBlank(){
            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidSize(){
            person.setPhoneNumber("+502 555 555 555 555");
            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("size must be between 0 and 16");
        }

        @Test
        void testValidSize(){
            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class BirthDateFieldTest{

        @BeforeEach
        void setUp(){
            person = givenPersonRequestDtoOne().orElseThrow();
        }

        @Test
        void testInvalidNotNull(){
            person.setBirthDate(null);
            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be null");
        }

        @Test
        void testValidNotNull(){
            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidPastOrPresent(){
            person.setBirthDate(LocalDate.parse("2028-09-02"));
            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must be a date in the past or in the present");
        }

        @Test
        void testValidPastOrPresent(){
            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class GenderFieldTest{

        @BeforeEach
        void setUp(){
            person = givenPersonRequestDtoOne().orElseThrow();
        }

        @Test
        void testInvalidNotBlank(){
            person.setGender(null);
            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be blank");

        }

        @Test
        void testValidNotBlank(){
            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidCorrectGender(){
            person.setGender("WOMAN");
            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("{dto.validation.CorrectGender}");
        }

        @Test
        void testValidCorrectGender(){
            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class HeightFieldTest{

        @BeforeEach
        void setUp(){
            person = givenPersonRequestDtoOne().orElseThrow();
        }

        @Test
        void testInvalidDecimalMin(){
            person.setHeight(0.49);
            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must be greater than or equal to 0.50");
        }

        @Test
        void testValidDecimalMin(){
            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidDecimalMax(){
            person.setHeight(4.00);
            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must be less than or equal to 3.00");
        }

        @Test
        void testValidDecimalMax(){
            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidDigits(){
            person.setHeight(2.24543334);
            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("{dto.validation.annotation.digits.height}");
        }

        @Test
        void testValidDigits(){
            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class WeightFieldTest{

        @BeforeEach
        void setUp(){
            person = givenPersonRequestDtoOne().orElseThrow();
        }

        @Test
        void testInvalidDecimalMin(){
            person.setWeight(19.0);
            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must be greater than or equal to 20.0");
        }

        @Test
        void testValidDecimalMin(){
            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidDecimalMax(){
            person.setWeight(220.0);
            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must be less than or equal to 200.0");
        }

        @Test
        void testValidDecimalMax(){
            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidDigits(){
            person.setWeight(100.222);
            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("{dto.validation.annotation.digits.weight}");
        }

        @Test
        void testValidDigits(){
            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }


    }

    


    
}
