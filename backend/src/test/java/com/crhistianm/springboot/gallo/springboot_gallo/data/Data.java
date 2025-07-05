package com.crhistianm.springboot.gallo.springboot_gallo.data;


import java.util.Optional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Role;

public class Data {

    public static Optional<AccountDto> createAccountUserDto(){
        return Optional.of(new AccountDto("erikuser@gmail.com", "12345", 1L, false));
    }

    public static Optional<AccountDto> createAccountAdminDto(){
        return Optional.of(new AccountDto("erikadmin@gmail.com", "12345", 2L, true));
    }

    public static Optional<Person> createPerson(){
        return Optional.of(new Person(1L, "ejemplo"));
    }

    public static Optional<Role> createAdminRole(){
        return Optional.of(new Role("ROLE_ADMIN"));
    }

    public static Optional<Role> createUserRole(){
        return Optional.of(new Role("ROLE_USER"));
    }




}
