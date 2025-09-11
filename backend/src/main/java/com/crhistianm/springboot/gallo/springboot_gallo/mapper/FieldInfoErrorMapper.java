package com.crhistianm.springboot.gallo.springboot_gallo.mapper;


import java.lang.reflect.Field;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.FieldInfoErrorBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;

public class FieldInfoErrorMapper {

    public static FieldInfoError classTargetToFieldInfo(Object targetClass, String fieldTargetName, String errorMessage) {
        Field field;
        FieldInfoError fieldInfo = null;

        try {
            field = targetClass.getClass().getDeclaredField(fieldTargetName);
            field.setAccessible(true);
            fieldInfo = new FieldInfoErrorBuilder()
                .name(field.getName())
                .type(field.getType())
                .value(field.get(targetClass))
                .ownerClass(targetClass.getClass())
                .errorMessage(errorMessage)
                .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fieldInfo;
    }

    
}
