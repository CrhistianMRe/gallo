package com.crhistianm.springboot.gallo.springboot_gallo.person;

import java.time.LocalDate;

import com.crhistianm.springboot.gallo.springboot_gallo.account.Account;

class PersonBuilder {

    private Long id;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private LocalDate birthDate;

    private String gender;

    private Double height;

    private Double weight;

    private Account account;

    PersonBuilder(){}

    PersonBuilder id(Long id){
        this.id = id;
        return this;
    }

    PersonBuilder firstName(String firstName){
        this.firstName = firstName;
        return this;
    }

    PersonBuilder lastName(String lastName){
        this.lastName = lastName;
        return this;
    }

    PersonBuilder phoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
        return this;
    }

    PersonBuilder birthDate(LocalDate birthDate){
        this.birthDate = birthDate;
        return this;
    }

    PersonBuilder gender(String gender){
        this.gender = gender;
        return this;
    }

    PersonBuilder height(Double height){
        this.height = height;
        return this;
    }

    PersonBuilder weight(Double weight){
        this.weight = weight;
        return this;
    }

    PersonBuilder account(Account account){
        this.account = account;
        return this;
    }

    Person build(){
        Person person = new Person();
        person.setId(this.id);
        person.setFirstName(this.firstName);
        person.setLastName(this.lastName);
        person.setPhoneNumber(this.phoneNumber);
        person.setBirthDate(this.birthDate);
        person.setGender(this.gender);
        person.setHeight(this.height);
        person.setWeight(this.weight);
        person.setAccount(this.account);
        return person;
    }

}
