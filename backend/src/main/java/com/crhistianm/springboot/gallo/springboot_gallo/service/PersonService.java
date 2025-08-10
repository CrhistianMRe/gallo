package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.List;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonCreateDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonResponseDto;

public interface PersonService {
   
    PersonResponseDto save(PersonCreateDto createDto);

    boolean isPhoneNumberAvailable(String phoneNumber);

    boolean isPersonRegistered(Long personId);

    List<PersonResponseDto> getAll();

    PersonResponseDto getById(Long id);

}

