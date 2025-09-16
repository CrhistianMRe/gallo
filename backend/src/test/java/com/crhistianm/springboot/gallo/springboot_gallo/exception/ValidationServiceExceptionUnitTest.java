package com.crhistianm.springboot.gallo.springboot_gallo.exception;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenFieldInfoErrorOne;
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenFieldInfoErrorTwo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;


public class ValidationServiceExceptionUnitTest {

    ValidationServiceException validationException;

    @Test
    void shouldLoadFieldInfoErrorListWhenIsPassedThroughConstructor() {
        List<FieldInfoError> fields = new ArrayList<>();

        fields.add(givenFieldInfoErrorOne().orElseThrow());
        fields.add(givenFieldInfoErrorTwo().orElseThrow());


        validationException = assertThatExceptionOfType(ValidationServiceException.class).isThrownBy(() ->{
            throw new ValidationServiceException();
        }).actual();

        FieldInfoError field1 = fields.stream().filter(error -> error.getName().equals("test1")).findFirst().orElseThrow();
        FieldInfoError field2 = fields.stream().filter(error -> error.getName().equals("test2")).findFirst().orElseThrow();
        


        assertThat(field1).extracting(FieldInfoError::getName).isEqualTo("test1");
        assertThat(field1).extracting(FieldInfoError::getValue).isEqualTo("testvalue1");
        assertThat(field1).extracting(FieldInfoError::getType).isEqualTo(String.class);
        assertThat(field1).extracting(FieldInfoError::getOwnerClass).isEqualTo(String.class);
        assertThat(field1).extracting(FieldInfoError::getErrorMessage).isEqualTo("test error message 1");

        assertThat(field2).extracting(FieldInfoError::getName).isEqualTo("test2");
        assertThat(field2).extracting(FieldInfoError::getValue).isEqualTo(2L);
        assertThat(field2).extracting(FieldInfoError::getType).isEqualTo(Long.class);
        assertThat(field2).extracting(FieldInfoError::getOwnerClass).isEqualTo(Long.class);
        assertThat(field2).extracting(FieldInfoError::getErrorMessage).isEqualTo("test error message 2");
    }

    @Test
    void shouldLoadMethodSourceNameWhenPrefixIsPassedThroughConstructor() {
        validationException = assertThatExceptionOfType(ValidationServiceException.class).isThrownBy(()-> {
            throw new ValidationServiceException(null, "should");
        }).actual();
        assertThat(validationException.getMethodSourceName()).isEqualTo("shouldLoadMethodSourceNameWhenPrefixIsPassedThroughConstructor");
    }
    
}
