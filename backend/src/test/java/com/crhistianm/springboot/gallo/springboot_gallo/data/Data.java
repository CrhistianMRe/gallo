package com.crhistianm.springboot.gallo.springboot_gallo.data;


import java.util.Optional;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.AccountDtoBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.builder.PersonBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.builder.RoleBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Role;

public class Data {

    public static Optional<AccountDto> createAccountUserDto(){
        return Optional.of(new AccountDtoBuilder().email("erikuser@gmail.com").password("12345").personId(1L).admin(false).build());
    }

    public static Optional<AccountDto> createAccountAdminDto(){
        return Optional.of(new AccountDtoBuilder().email("erikadmin@gmail.com").password("12345").personId(2L).admin(true).build());
    }

    public static Optional<Person> createPerson(){
        return Optional.of(new PersonBuilder().id(1L).firstName("ejemplo").lastName("ejemploLast").build());
    }

    public static Optional<Role> createAdminRole(){
        return Optional.of(new RoleBuilder().name("ROLE_ADMIN").build());
    }

    public static Optional<Role> createUserRole(){
        return Optional.of(new RoleBuilder().name("ROLE_USER").build());
    }

}
