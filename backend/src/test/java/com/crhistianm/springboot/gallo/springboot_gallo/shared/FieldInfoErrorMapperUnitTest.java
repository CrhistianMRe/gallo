package com.crhistianm.springboot.gallo.springboot_gallo.shared;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.Data;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.Data.DummyBaseClass;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.Data.SampleClass;

class FieldInfoErrorMapperUnitTest {

    @Nested
    class ClassTargetToFieldInfo {

        FieldInfoError field;

        @BeforeEach
        void setUp(){
            field = new FieldInfoError();
        }

        @Test
        void returnsPrimitiveBooleanField() {
            SampleClass sClass = new SampleClass();
            field = FieldInfoErrorMapper.classTargetToFieldInfo(sClass, "testPrimitiveBoolean", "test");

            assertThat(field.getName()).isEqualTo("testPrimitiveBoolean");
            assertThat(String.valueOf(field.getType())).isEqualTo("boolean");
            assertThat(field.getType()).isPrimitive();
            assertThat(field.getValue()).isEqualTo(false);
            assertThat(field.getErrorMessage()).isEqualTo("test");
            assertThat(field.getOwnerClass()).isEqualTo(Data.SampleClass.class);
        }

        @Test
        void returnsObjectField() {
            SampleClass sClass = new SampleClass();
            field = FieldInfoErrorMapper.classTargetToFieldInfo(sClass, "testObjectString", "test");

            assertThat(field.getName()).isEqualTo("testObjectString");
            assertThat(field.getType()).isEqualTo(String.class);
            assertThat(field.getValue()).isEqualTo(null);
            assertThat(field.getErrorMessage()).isEqualTo("test");
            assertThat(field.getOwnerClass()).isEqualTo(SampleClass.class);
        }

        @Test
        void returnsSuperObjectField() {
            SampleClass sClass = new SampleClass();
            field = FieldInfoErrorMapper.classTargetToFieldInfo(sClass, "surnames", "test");

            assertThat(field).extracting(
                    FieldInfoError::getName, 
                    FieldInfoError::getType, 
                    FieldInfoError::getValue,
                    FieldInfoError::getErrorMessage,
                    FieldInfoError::getOwnerClass)
                .contains("surnames", List.class, new ArrayList<>(), "test", DummyBaseClass.class);
        }

        @Test
        void returnsNullWhenFieldIsNotFound() {
            field = FieldInfoErrorMapper.classTargetToFieldInfo(SampleClass.class, "notfound", "test");
            assertThat(field).isNull();
        }

    }
    


    
}
