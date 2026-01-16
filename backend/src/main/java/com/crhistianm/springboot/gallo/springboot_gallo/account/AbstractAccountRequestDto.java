package com.crhistianm.springboot.gallo.springboot_gallo.account;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.RequestDto;

//This class is temporarily public as the migration is ongoing
abstract class AbstractAccountRequestDto implements RequestDto {

    private String email;

    private String password;

    private Long personId;

    AbstractAccountRequestDto(String email, String password, Long personId) {
        this.email = email;
        this.password = password;
        this.personId = personId;
    }

    String getEmail() {
        return email;
    }

    void setEmail(String email) {
        this.email = email;
    }

    String getPassword() {
        return password;
    }

    void setPassword(String password) {
        this.password = password;
    }

    Long getPersonId() {
        return personId;
    }

    void setPersonId(Long personId) {
        this.personId = personId;
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract String toString();


    








    
}
