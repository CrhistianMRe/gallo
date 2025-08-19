package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.List;
import java.util.Optional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonResponseDto;

public interface PersonService {
   
    PersonResponseDto save(PersonRequestDto createDto);

    Optional<PersonResponseDto> update(Long id, PersonRequestDto personDto);

    Optional<PersonResponseDto> delete(Long id);

    boolean isPhoneNumberAvailable(String phoneNumber);

    boolean isPersonRegistered(Long personId);

    List<PersonResponseDto> getAll();

    Optional<PersonResponseDto> getById(Long id);


}

