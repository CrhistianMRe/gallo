package com.crhistianm.springboot.gallo.springboot_gallo.data;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Arrays;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.AccountBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.builder.RoleBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountAdminResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountCreateDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonCreateDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Audit;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Role;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.PersonMapper;

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

    public static Optional<Account> createAccountAdmin(){
        Audit audit = new Audit();
        audit.setEnabled(true);
        audit.setCreatedAt(LocalDateTime.of(2004, 01, 01, 1, 1, 1, 1));
        audit.setUpdatedAt(LocalDateTime.of(2004, 02, 01, 1, 1, 1, 1));
        
        return Optional.of(new AccountBuilder()
                .id(1L)
                .email("admin@gmail.com")
                .password("12345")
                .person(PersonMapper.createToEntity(createPersonOneDto().orElseThrow()))
                .roles(List.of(createAdminRole().orElseThrow(), createUserRole().orElseThrow()))
                .audit(audit)
                .workouts(null)
                .build()
                );
    }

    public static Optional<Account> createAccountUser(){
        Audit audit = new Audit();
        audit.setEnabled(true);
        audit.setCreatedAt(LocalDateTime.of(2004, 01, 01, 1, 1, 1, 1));
        audit.setUpdatedAt(LocalDateTime.of(2004, 02, 01, 1, 1, 1, 1));
        
        return Optional.of(new AccountBuilder()
                .id(1L)
                .email("user@gmail.com")
                .password("12345")
                .person(PersonMapper.createToEntity(createPersonOneDto().orElseThrow()))
                .roles(List.of(createUserRole().orElseThrow()))
                .audit(audit)
                .workouts(null)
                .build()
                );
    }

}
