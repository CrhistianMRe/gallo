package com.crhistianm.springboot.gallo.springboot_gallo.service;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;

public interface PersonValidationService {

    boolean isPhoneNumberAvailable(Long pathPersonId, String phoneNumber);

    boolean isPersonRegistered(Long pathPersonId);

    void validateRequest(Long pathPersonid, PersonRequestDto personDto);

}
