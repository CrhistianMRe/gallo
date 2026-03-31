package com.crhistianm.springboot.gallo.springboot_gallo.account;


abstract class AccountResponseDto {

    protected String email;

    protected Long personId;

    String getEmail(){
        return this.email;
    }

    void setEmail(String email) {
        this.email = email;
    }

    Long getPersonId() {
        return personId;
    }

    void setPersonId(Long personId) {
        this.personId = personId;
    }

}
