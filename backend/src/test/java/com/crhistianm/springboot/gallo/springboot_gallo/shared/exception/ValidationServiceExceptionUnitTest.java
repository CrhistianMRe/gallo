package com.crhistianm.springboot.gallo.springboot_gallo.shared.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorBuilder;

class ValidationServiceExceptionUnitTest {

    private ValidationServiceException validationException;

    @Test
    void shouldReturnExceptionWithDefaultMessage() {

       validationException = new ValidationServiceException();

       assertThat(validationException)
           .extracting(ValidationServiceException::getMessage)
           .isEqualTo("Validation failed");
    }

    @Test
    void shouldLoadErrorList() {
        final String methodSourcePrefix = "should";
        List<FieldInfoError> errorList = new ArrayList<>();

        FieldInfoError errorOne = new FieldInfoErrorBuilder().name("errorOne").build();
        FieldInfoError errorTwo = new FieldInfoErrorBuilder().name("errorTwo").build();

        errorList.add(errorOne);
        errorList.add(errorTwo);

        validationException = new ValidationServiceException(errorList, methodSourcePrefix);

        List<FieldInfoError> expectedList = validationException.getFieldErrors();

        assertThat(expectedList).hasSize(2);
    }

}
