package com.crhistianm.springboot.gallo.springboot_gallo.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;

public class ValidationServiceErrorList extends ArrayList<FieldInfoError> {

    public ValidationServiceErrorList(){super();}

    public ValidationServiceErrorList(Collection<FieldInfoError> c) {
        super(c);
    }

    public void throwFieldErrors() {
        if(!this.isEmpty()) {
            throw new ValidationServiceException(List.copyOf(this));
        }
    }
    
}
