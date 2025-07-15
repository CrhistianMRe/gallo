package com.crhistianm.springboot.gallo.springboot_gallo.service;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonCreateDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonResponseDto;

public interface PersonService {
   
    PersonResponseDto save(PersonCreateDto createDto);

}

