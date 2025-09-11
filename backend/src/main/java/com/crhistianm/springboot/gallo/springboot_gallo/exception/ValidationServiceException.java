package com.crhistianm.springboot.gallo.springboot_gallo.exception;

import java.util.ArrayList;
import java.util.List;


import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;

//prefix by default is validate as is the one used on service
public class ValidationServiceException extends TraceException  {

    private List<FieldInfoError> fieldErrors;

    public ValidationServiceException() {
        super("Validation failed", "validate");
    }

    public ValidationServiceException(String message) {
        super(message, "validate");
    }

    public ValidationServiceException(List<FieldInfoError> fieldList, String prefixMethod) {
        super("Validation failed", prefixMethod);
        loadErrorList(fieldList);
    }

    public ValidationServiceException(List<FieldInfoError> fieldList) {
        super("Validation failed", "validate");
        loadErrorList(fieldList);
    }

    public ValidationServiceException(String message, String sourceMethodName, List<FieldInfoError> fieldList){
        super(message, sourceMethodName);
        loadErrorList(fieldList);
    }

    public ValidationServiceException(String message, List<FieldInfoError> fieldList) {
        super(message, "valiate");
        loadErrorList(fieldList);
    }

    private void loadErrorList(List<FieldInfoError> fieldErrorList){
        this.fieldErrors = new ArrayList<>();
        this.fieldErrors = fieldErrorList;
    }

    public List<FieldInfoError> getFieldErrors() {
        return fieldErrors;
    }

    public List<FieldInfoError> addField(FieldInfoError field){
        fieldErrors.add(field);
        return this.fieldErrors;
    }
    
}
