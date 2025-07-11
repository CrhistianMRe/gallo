package com.crhistianm.springboot.gallo.springboot_gallo.builder;

import java.time.LocalDate;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;

public class PersonBuilder {

    private Long id;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private LocalDate birthDate;

    private String gender;

    private Double height;

    private Double weight;

    private Account account;

    public PersonBuilder(){}

    public PersonBuilder id(Long id){
        this.id = id;
        return this;
    }

    public PersonBuilder firstName(String firstName){
        this.firstName = firstName;
        return this;
    }

    public PersonBuilder lastName(String lastName){
        this.lastName = lastName;
        return this;
    }

    public PersonBuilder phoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
        return this;
    }

    public PersonBuilder birthDate(LocalDate birthDate){
        this.birthDate = birthDate;
        return this;
    }

    public PersonBuilder gender(String gender){
        this.gender = gender;
        return this;
    }

    public PersonBuilder height(Double height){
        this.height = height;
        return this;
    }

    public PersonBuilder weight(Double weight){
        this.weight = weight;
        return this;
    }

    public PersonBuilder account(Account account){
        this.account = account;
        return this;
    }

    public Person build(){
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
