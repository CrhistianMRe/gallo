package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.Optional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AbstractAccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;

public interface IdentityVerificationService {

    boolean isAdminAuthority();

    boolean isUserPersonEntityAllowed(Long id);

    public Optional<FieldInfoError> validateUserAllowance(Long pathPersonId);

    public Optional<FieldInfoError> validateAdminRequired(AbstractAccountRequestDto requestDto, String fieldName);

}
