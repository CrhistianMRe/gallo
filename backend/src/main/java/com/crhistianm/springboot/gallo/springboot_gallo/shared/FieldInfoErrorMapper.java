package com.crhistianm.springboot.gallo.springboot_gallo.shared;


import java.lang.reflect.Field;

public class FieldInfoErrorMapper {

    public static FieldInfoError classTargetToFieldInfo(Object targetClass, String fieldTargetName, String errorMessage) {
        Field field;
        FieldInfoError fieldInfo = null;
        boolean isSuperField = true;
        Class<?> selectedClass = targetClass.getClass();

        try {
            for (Field f : targetClass.getClass().getDeclaredFields()) {
                if(f.getName().equals(fieldTargetName)) isSuperField = false;
            }
            if(isSuperField) selectedClass = selectedClass.getSuperclass();

            field = selectedClass.getDeclaredField(fieldTargetName);
            field.setAccessible(true);
            fieldInfo = new FieldInfoErrorBuilder()
                .name(field.getName())
                .type(field.getType())
                .value(field.get(targetClass))
                .ownerClass(selectedClass)
                .errorMessage(errorMessage)
                .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fieldInfo;
    }

    
}
