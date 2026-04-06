package com.crhistianm.springboot.gallo.springboot_gallo.person;

import static com.crhistianm.springboot.gallo.springboot_gallo.person.PersonData.givenPersonRequestDtoOne;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.config.ValidatorConfig;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = { LocalValidatorFactoryBean.class }) it does the same as below
@Import(ValidatorConfig.class)
class PersonRequestDtoAnnotationTest {

    @Autowired
    private Validator validator;

    private PersonRequestDto person;

    Set<ConstraintViolation<PersonRequestDto>> violations;

    @Nested
    class FirstNameFieldTest{
        
        @BeforeEach
        void setUp(){
            person = givenPersonRequestDtoOne().orElseThrow();
        }

        @Test
        void testInvalidNotBlank(){
            String firstName = "";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be blank");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("firstName");
        }

        @Test
        void testValidNotBlank(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidSize(){
            String firstName = "somenametoolongsomenametoolongsomenametoolongsomenametoolong";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("size must be between 0 and 45");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("firstName");
        }

        @Test
        void testValidSize(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class LastNameFieldTest{

        @Test
        void testInvalidNotBlank(){
            String firstName = "one";
            String lastName = "";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be blank");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("lastName");
        }

        @Test
        void testValidNotBlank(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidSize(){
            String firstName = "one";
            String lastName = "somenametoolongsomenametoolongsomenametoolongsomenametoolong";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("size must be between 0 and 45");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("lastName");
        }

        @Test
        void testValidSize(){
            person = givenPersonRequestDtoOne().orElseThrow();
            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class PhoneNumberFieldTest{
        
        @Test
        void testInvalidNotBlank(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be blank");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("phoneNumber");
        }

        @Test
        void testValidNotBlank(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidSize(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "+502 555 555 555 555";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("size must be between 0 and 16");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("phoneNumber");
        }
        

        @Test
        void testValidSize(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }
    }

    

    @Nested
    class BirthDateFieldTest{

        @Test
        void testInvalidNotNull(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = null;
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be null");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("birthDate");
        }

        @Test
        void testValidNotNull(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidPastOrPresent(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = LocalDate.now().plusYears(1);
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must be a date in the past or in the present");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("birthDate");
        }

        @Test
        void testValidPastOrPresent(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class GenderFieldTest{

        @Test
        void testInvalidNotBlank(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = null;
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must not be blank");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("gender");

        }

        @Test
        void testValidNotBlank(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidCorrectGender(){

            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "A";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("{dto.validation.CorrectGender}");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("gender");
        }

        @Test
        void testValidCorrectGender(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class HeightFieldTest{

        @Test
        void testInvalidDecimalMin(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = 0.49;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must be greater than or equal to 0.50");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("height");
        }

        @Test
        void testValidDecimalMin(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidDecimalMax(){

            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = 4.00;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must be less than or equal to 3.00");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("height");
        }

        @Test
        void testValidDecimalMax(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidDigits(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = 2.24543334;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("{dto.validation.annotation.digits.height}");
        }

        @Test
        void testValidDigits(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

    }

    @Nested
    class WeightFieldTest{

        @Test
        void testInvalidDecimalMin(){

            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = 19.0;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must be greater than or equal to 20.0");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("weight");
        }

        @Test
        void testValidDecimalMin(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidDecimalMax(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = 220.0;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("must be less than or equal to 200.0");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("weight");
        }

        @Test
        void testValidDecimalMax(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

        @Test
        void testInvalidDigits(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = 100.222;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertFalse(violations.isEmpty());
            assertThat(violations).hasSize(1);
            assertThat(violations).extracting(ConstraintViolation::getMessage).containsOnly("{dto.validation.annotation.digits.weight}");
            assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("weight");
        }

        @Test
        void testValidDigits(){
            String firstName = "one";
            String lastName = "1one";
            LocalDate birthDate = (LocalDate.of(2004, 01, 01));
            String gender = "M";
            String phoneNumber = "123123123";
            Double height = null;
            Double weight = null;

            person = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

            violations = validator.validate(person);
            assertTrue(violations.isEmpty());
            assertThat(violations).hasSize(0);
        }

    }
    
}
