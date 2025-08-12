package com.crhistianm.springboot.gallo.springboot_gallo.mapper;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.PersonBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountAdminResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;

public class PersonMapper {

    public static Person createToEntity(PersonRequestDto dto){
        return new PersonBuilder().firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .phoneNumber(dto.getPhoneNumber())
            .birthDate(dto.getBirthDate())
            .gender(dto.getGender())
            .height(dto.getHeight())
            .weight(dto.getWeight())
            .build();
    }


    public static PersonResponseDto entityToResponse(Person person){
        PersonResponseDto dto = new PersonResponseDto();
        dto.setId(person.getId());
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setPhoneNumber(person.getPhoneNumber());
        dto.setBirthDate(person.getBirthDate());
        dto.setGender(person.getGender());
        dto.setHeight(person.getHeight());
        dto.setWeight(person.getWeight());
        if(person.getAccount()!=null)dto.setAccount((AccountAdminResponseDto)AccountMapper.entityToAdminResponse(person.getAccount()));
        return dto;
    }
}
