package com.crhistianm.springboot.gallo.springboot_gallo.person;

import java.time.LocalDate;
import java.util.Optional;

public class PersonData {

    public static Person getPersonInstance(){
        return new Person();
    }
    
    static Optional<PersonRequestDto> givenPersonRequestDtoOne(){
        String firstName = "one";
        String lastName = "1one";
        LocalDate birthDate = (LocalDate.of(2004, 01, 01));
        String gender = "M";
        String phoneNumber = "123123123";
        Double height = null;
        Double weight = null;
        return Optional.of(new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight));
    }

    static Optional<PersonRequestDto> givenPersonRequestDtoTwo(){
        String firstName = "two";
        String lastName = "2two";
        LocalDate birthDate = (LocalDate.now());
        String gender = "M";
        String phoneNumber = "123123123";
        Double height = null;
        Double weight = null;
        return Optional.of(new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight));
    }

    static Optional<Person> givenPersonEntityOne(){
        return Optional.of(new PersonBuilder()
                .id(1L)
                .firstName("Crhistian")
                .lastName("Mendez")
                .phoneNumber("4444444")
                .birthDate(LocalDate.of(2004, 9, 28))
                .gender("M")
                .height(1.74)
                .weight(80.0)
                .build());
    }

    static Optional<Person> givenPersonEntityTwo(){
        return Optional.of(new PersonBuilder()
                .id(2L)
                .firstName("Erick")
                .lastName("Perez")
                .phoneNumber("55896144")
                .birthDate(LocalDate.of(2005, 2, 1))
                .gender("M")
                .height(1.76)
                .weight(80.0)
                .build());
    }

}
