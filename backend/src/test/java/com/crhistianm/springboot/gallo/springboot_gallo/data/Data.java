package com.crhistianm.springboot.gallo.springboot_gallo.data;


import java.time.LocalDate;
import java.util.Optional;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.RoleBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountCreateDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonCreateDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Role;

public class Data {

    public static Optional<AccountCreateDto> createAccountDto(){
        AccountCreateDto accountCreateDto = new AccountCreateDto();
        accountCreateDto.setEmail("erikuser@gmail.com");
        accountCreateDto.setPassword("12345");
        accountCreateDto.setPersonId(1L);
        return Optional.of(accountCreateDto);
    }

    public static Optional<AccountCreateDto> createAccountAdminDto(){
        AccountCreateDto accountCreateDto = new AccountCreateDto();
        accountCreateDto.setEmail("erikadmin@gmail.com");
        accountCreateDto.setPassword("12345");
        accountCreateDto.setPersonId(2L);
        accountCreateDto.setAdmin(true);
        return Optional.of(accountCreateDto);
    }

    public static Optional<PersonCreateDto> createPersonOneDto(){
        PersonCreateDto person = new PersonCreateDto();
        person.setFirstName("one");
        person.setLastName("1one");
        person.setBirthDate(LocalDate.now());
        person.setGender("M");
        person.setPhoneNumber("123123123");
        return Optional.of(person);
    }

    public static Optional<PersonCreateDto> createPersonTwoDto(){
        PersonCreateDto person = new PersonCreateDto();
        person.setFirstName("two");
        person.setLastName("2two");
        person.setBirthDate(LocalDate.now());
        person.setGender("M");
        person.setPhoneNumber("123123123");
        return Optional.of(person);
    }

    public static Optional<Role> createAdminRole(){
        return Optional.of(new RoleBuilder().name("ROLE_ADMIN").build());
    }

    public static Optional<Role> createUserRole(){
        return Optional.of(new RoleBuilder().name("ROLE_USER").build());
    }

}
