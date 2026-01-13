package com.crhistianm.springboot.gallo.springboot_gallo.person;

class PersonMapper {

    static Person requestToEntity(PersonRequestDto dto){
        return new PersonBuilder().firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .phoneNumber(dto.getPhoneNumber())
            .birthDate(dto.getBirthDate())
            .gender(dto.getGender())
            .height(dto.getHeight())
            .weight(dto.getWeight())
            .build();
    }


    static PersonResponseDto entityToResponse(Person person){
        PersonResponseDto dto = new PersonResponseDto();
        dto.setId(person.getId());
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setPhoneNumber(person.getPhoneNumber());
        dto.setBirthDate(person.getBirthDate());
        dto.setGender(person.getGender());
        dto.setHeight(person.getHeight());
        dto.setWeight(person.getWeight());
        return dto;
    }
}
