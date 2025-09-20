package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.List;


import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonResponseDto;

public interface PersonService {
   
    PersonResponseDto save(PersonRequestDto createDto);

    PersonResponseDto update(Long id, PersonRequestDto personDto);

    PersonResponseDto delete(Long id);

    PersonResponseDto getById(Long id);

    List<PersonResponseDto> getAll();

}

