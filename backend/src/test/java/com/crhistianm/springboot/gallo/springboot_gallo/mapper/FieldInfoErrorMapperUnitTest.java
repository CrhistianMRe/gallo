package com.crhistianm.springboot.gallo.springboot_gallo.mapper;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import com.crhistianm.springboot.gallo.springboot_gallo.data.Data;
import com.crhistianm.springboot.gallo.springboot_gallo.data.Data.SampleClass;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;

public class FieldInfoErrorMapperUnitTest {

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
        void returnsNullWhenFieldIsNotFound() {
            field = FieldInfoErrorMapper.classTargetToFieldInfo(SampleClass.class, "notfound", "test");
            assertThat(field).isNull();
        }

    }
    


    
}
