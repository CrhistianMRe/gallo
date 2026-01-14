package com.crhistianm.springboot.gallo.springboot_gallo.shared;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.NotEmptyRequest;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.NotEmptyRequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.Data.ChildClass;

import jakarta.validation.Payload;

class NotEmptyRequestValidatorUnitTest {

    private NotEmptyRequestValidator validator;

    private static NotEmptyRequest givenNotEmptyRequestAnnotation (boolean hasSuper){
        return new NotEmptyRequest() {

            @Override
            public boolean hasSuper() {
                return hasSuper;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return null;
            }
            @Override
            public String message() {
                return null;
            }
            @Override
            public Class<?>[] groups() {
                return null;
            }
        };
    }

    ChildClass childClass;

    @BeforeEach
    void setUp() {
        childClass = new ChildClass();
        validator = new NotEmptyRequestValidator();
        validator.initialize(givenNotEmptyRequestAnnotation(true));
    }

    @Test
    void shouldReturnFalseWhenAllFieldsAreNullOrEmpty(){
        assertThat(validator.isValid(childClass, null)).isFalse();
    }

    @Test
    void shouldReturnTrueWhenSuperWrapperFieldIsPresent() {
        childClass.setName("email");
        assertThat(validator.isValid(childClass, null)).isTrue();
    }

    @Test
    void shouldReturnTrueWhenChildWrapperFieldIsPresent() {
        childClass.setHasLicense(true);
        assertThat(validator.isValid(childClass, null)).isTrue();
    }

    @Test
    void shouldReturnTrueWhenChildCollectionIsNotEmpty() {
        childClass.setCars(new HashSet<>(Set.of("")));
        assertThat(validator.isValid(childClass, null)).isTrue();
    }

    @Test
    void shouldReturnTrueWhenSuperCollectionIsNotEmpty() {
        childClass.setSurnames(new ArrayList<>(List.of("")));
        assertThat(validator.isValid(childClass, null)).isTrue();
    }


}
