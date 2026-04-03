package com.crhistianm.springboot.gallo.springboot_gallo.account;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.RequestDto;

//This class is temporarily public as the migration is ongoing
abstract class AbstractAccountRequestDto implements RequestDto {

    private final String email;

    private final String password;

    private final Long personId;

    AbstractAccountRequestDto(String email, String password, Long personId) {
        this.email = email;
        this.password = password;
        this.personId = personId;
    }

    String getEmail() {
        return email;
    }

    String getPassword() {
        return password;
    }

    Long getPersonId() {
        return personId;
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract String toString();

}
