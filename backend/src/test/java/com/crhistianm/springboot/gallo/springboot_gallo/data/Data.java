package com.crhistianm.springboot.gallo.springboot_gallo.data;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import com.crhistianm.springboot.gallo.springboot_gallo.builder.AccountBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.builder.PersonBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.builder.RoleBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Audit;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Role;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.PersonMapper;

public class Data {

    public static Optional<AccountRequestDto> givenUserAccountRequestDto(){
        AccountRequestDto accountCreateDto = new AccountRequestDto();
        accountCreateDto.setEmail("erikuser@gmail.com");
        accountCreateDto.setPassword("12345");
        accountCreateDto.setPersonId(1L);
        return Optional.of(accountCreateDto);
    }

    public static Optional<AccountRequestDto> givenAdminAccountRequestDto(){
        AccountRequestDto accountCreateDto = new AccountRequestDto();
        accountCreateDto.setEmail("erikadmin@gmail.com");
        accountCreateDto.setPassword("12345");
        accountCreateDto.setPersonId(2L);
        accountCreateDto.setAdmin(true);
        return Optional.of(accountCreateDto);
    }

    public static Optional<PersonRequestDto> givenPersonRequestDtoOne(){
        PersonRequestDto person = new PersonRequestDto();
        person.setFirstName("one");
        person.setLastName("1one");
        person.setBirthDate(LocalDate.now());
        person.setGender("M");
        person.setPhoneNumber("123123123");
        return Optional.of(person);
    }

    public static Optional<PersonRequestDto> givenPersonRequestDtoTwo(){
        PersonRequestDto person = new PersonRequestDto();
        person.setFirstName("two");
        person.setLastName("2two");
        person.setBirthDate(LocalDate.now());
        person.setGender("M");
        person.setPhoneNumber("123123123");
        return Optional.of(person);
    }

    public static Optional<Role> givenRoleAdmin(){
        return Optional.of(new RoleBuilder().name("ROLE_ADMIN").build());
    }

    public static Optional<Role> givenRoleUser(){
        return Optional.of(new RoleBuilder().name("ROLE_USER").build());
    }

    public static Optional<Account> givenAccountEntityAdmin(){
        Audit audit = new Audit();
        audit.setEnabled(true);
        audit.setCreatedAt(LocalDateTime.of(2004, 01, 01, 1, 1, 1, 1));
        audit.setUpdatedAt(LocalDateTime.of(2004, 02, 01, 1, 1, 1, 1));
        
        return Optional.of(new AccountBuilder()
                .id(1L)
                .email("admin@gmail.com")
                .password("12345")
                .person(PersonMapper.requestToEntity(givenPersonRequestDtoOne().orElseThrow()))
                .roles(List.of(givenRoleAdmin().orElseThrow(), givenRoleUser().orElseThrow()))
                .audit(audit)
                .workouts(null)
                .build()
                );
    }

    public static Optional<Account> givenAccountEntityUser(){
        Audit audit = new Audit();
        audit.setEnabled(true);
        audit.setCreatedAt(LocalDateTime.of(2004, 01, 01, 1, 1, 1, 1));
        audit.setUpdatedAt(LocalDateTime.of(2004, 02, 01, 1, 1, 1, 1));
        
        return Optional.of(new AccountBuilder()
                .id(1L)
                .email("user@gmail.com")
                .password("12345")
                .person(PersonMapper.requestToEntity(givenPersonRequestDtoOne().orElseThrow()))
                .roles(List.of(givenRoleUser().orElseThrow()))
                .audit(audit)
                .workouts(null)
                .build()
                );
    }

    public static Optional<Person> givenPersonEntityOne(){
        return Optional.of(new PersonBuilder()
                .id(1L)
                .firstName("Crhistian")
                .lastName("Mendez")
                .phoneNumber("4444444")
                .birthDate(LocalDate.of(2004, 9, 28))
                .gender("M")
                .height(1.74)
                .weight(80.0)
                .account(givenAccountEntityAdmin().orElseThrow())
                .build());
    }

    public static Optional<Person> givenPersonEntityTwo(){
        return Optional.of(new PersonBuilder()
                .id(2L)
                .firstName("Erick")
                .lastName("Perez")
                .phoneNumber("55896144")
                .birthDate(LocalDate.of(2005, 2, 1))
                .gender("M")
                .height(1.76)
                .weight(80.0)
                .account(givenAccountEntityUser().orElseThrow())
                .build());
    }

}
