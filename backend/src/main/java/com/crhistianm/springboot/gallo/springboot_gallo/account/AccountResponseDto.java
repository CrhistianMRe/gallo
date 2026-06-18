package com.crhistianm.springboot.gallo.springboot_gallo.account;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.ResponseDto;

abstract class AccountResponseDto implements ResponseDto {

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
