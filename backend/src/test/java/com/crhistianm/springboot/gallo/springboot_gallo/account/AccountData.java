package com.crhistianm.springboot.gallo.springboot_gallo.account;


import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.crhistianm.springboot.gallo.springboot_gallo.person.Person;
import static com.crhistianm.springboot.gallo.springboot_gallo.person.PersonData.getPersonInstance;

public class AccountData {

    static Optional<AccountRequestDto> givenUserAccountRequestDto(){
        AccountRequestDto accountCreateDto = new AccountRequestDto();
        accountCreateDto.setEmail("erikuser@gmail.com");
        accountCreateDto.setPassword("12345");
        accountCreateDto.setPersonId(1L);
        return Optional.of(accountCreateDto);
    }

    static Optional<AccountRequestDto> givenAdminAccountRequestDto(){
        AccountRequestDto accountCreateDto = new AccountRequestDto();
        accountCreateDto.setEmail("erikadmin@gmail.com");
        accountCreateDto.setPassword("12345");
        accountCreateDto.setPersonId(2L);
        accountCreateDto.setAdmin(true);
        return Optional.of(accountCreateDto);
    }


    static Optional<Role> givenRoleAdmin(){
        return Optional.of(new RoleBuilder().name("ROLE_ADMIN").build());
    }

    static Optional<Role> givenRoleUser(){
        return Optional.of(new RoleBuilder().name("ROLE_USER").build());
    }

    static Optional<RoleResponseDto> givenRoleResponseDtoAdmin(){
        return Optional.of(new RoleResponseDto(null, "ROLE_ADMIN"));
    }
    static Optional<RoleResponseDto> givenRoleResponseDtoUser(){
        return Optional.of(new RoleResponseDto(null, "ROLE_USER"));
    }

    public static Account getAccountInstance(){
        return new Account();
    }

    static Optional<Account> givenAccountEntityAdmin(){
        Audit audit = new Audit();
        audit.setEnabled(true);
        audit.setCreatedAt(LocalDateTime.of(2004, 01, 01, 1, 1, 1, 1));
        audit.setUpdatedAt(LocalDateTime.of(2004, 02, 01, 1, 1, 1, 1));

        Person person = getPersonInstance();
        
        return Optional.of(new AccountBuilder()
                .id(1L)
                .email("admin@gmail.com")
                .password("12345")
                .roles(List.of(givenRoleAdmin().orElseThrow(), givenRoleUser().orElseThrow()))
                .audit(audit)
                .person(person)
                .workouts(null)
                .build()
                );
    }

    static Optional<Account> givenAccountEntityUser(){
        Audit audit = new Audit();
        audit.setEnabled(true);
        audit.setCreatedAt(LocalDateTime.of(2004, 01, 01, 1, 1, 1, 1));
        audit.setUpdatedAt(LocalDateTime.of(2004, 02, 01, 1, 1, 1, 1));
        

        Person person = getPersonInstance();

        return Optional.of(new AccountBuilder()
                .id(1L)
                .email("user@gmail.com")
                .password("12345")
                .roles(List.of(givenRoleUser().orElseThrow()))
                .person(person)
                .audit(audit)
                .workouts(null)
                .build()
                );
    }

}
