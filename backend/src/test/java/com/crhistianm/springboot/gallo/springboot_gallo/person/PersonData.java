package com.crhistianm.springboot.gallo.springboot_gallo.person;

import java.time.LocalDate;
import java.util.Optional;

public class PersonData {

    public static Person getPersonInstance(){
        return new Person();
    }
    
    static Optional<PersonRequestDto> givenPersonRequestDtoOne(){
        PersonRequestDto person = new PersonRequestDto();
        person.setFirstName("one");
        person.setLastName("1one");
        person.setBirthDate(LocalDate.of(2004, 01, 01));
        person.setGender("M");
        person.setPhoneNumber("123123123");
        return Optional.of(person);
    }

    static Optional<PersonRequestDto> givenPersonRequestDtoTwo(){
        PersonRequestDto person = new PersonRequestDto();
        person.setFirstName("two");
        person.setLastName("2two");
        person.setBirthDate(LocalDate.now());
        person.setGender("M");
        person.setPhoneNumber("123123123");
        return Optional.of(person);
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
