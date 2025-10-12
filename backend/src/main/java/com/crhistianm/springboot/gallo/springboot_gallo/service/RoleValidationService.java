package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.List;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.RoleRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;

public interface RoleValidationService {

    List<FieldInfoError> validateRoleExists(RoleRequestDto role);
    
}
