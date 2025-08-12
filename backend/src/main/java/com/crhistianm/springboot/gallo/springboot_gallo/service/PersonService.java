package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.List;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonResponseDto;

public interface PersonService {
   
    PersonResponseDto save(PersonRequestDto createDto);

    boolean isPhoneNumberAvailable(String phoneNumber);

    boolean isPersonRegistered(Long personId);

    List<PersonResponseDto> getAll();

    PersonResponseDto getById(Long id);

}

