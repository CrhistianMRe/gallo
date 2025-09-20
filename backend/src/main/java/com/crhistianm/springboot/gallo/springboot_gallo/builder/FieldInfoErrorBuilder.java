package com.crhistianm.springboot.gallo.springboot_gallo.builder;

import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;

public class FieldInfoErrorBuilder {

    private String name;

    private Class<?> type;

    private Object value;

    private Class<?> ownerClass;

    private String errorMessage;

    public FieldInfoErrorBuilder() {
    }

    public FieldInfoErrorBuilder name(String name) {
        this.name = name;
        return this;
    }

    public FieldInfoErrorBuilder type(Class<?> type) {
        this.type = type;
        return this;
    }

    public FieldInfoErrorBuilder value(Object value){
        this.value = value;
        return this;
    }

    public FieldInfoErrorBuilder ownerClass(Class<?> ownerClass){
        this.ownerClass = ownerClass;
        return this;
    }

    public FieldInfoErrorBuilder errorMessage(String errorMessage){
        this.errorMessage = errorMessage;
        return this;
    }

    public FieldInfoError build(){
        FieldInfoError fieldInfo = new FieldInfoError();
        fieldInfo.setName(this.name);
        fieldInfo.setType(this.type);
        fieldInfo.setValue(this.value);
        fieldInfo.setOwnerClass(this.ownerClass);
        fieldInfo.setErrorMessage(this.errorMessage);
        return fieldInfo;
    }
    
}
