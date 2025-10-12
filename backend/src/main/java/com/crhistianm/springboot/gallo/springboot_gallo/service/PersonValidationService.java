package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.Optional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AbstractAccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;

public interface PersonValidationService {

    boolean isPhoneNumberAvailable(Long pathPersonId, String phoneNumber);

    boolean isPersonRegistered(Long pathPersonId);

    Optional<FieldInfoError> validateUniquePhoneNumber(Long pathPersonId, PersonRequestDto personDto);

    Optional<FieldInfoError> validatePersonRegistered(AbstractAccountRequestDto accountDto);

}
