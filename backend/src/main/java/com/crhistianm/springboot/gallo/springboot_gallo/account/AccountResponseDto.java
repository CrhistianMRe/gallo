package com.crhistianm.springboot.gallo.springboot_gallo.account;


abstract class AccountResponseDto {

    protected String email;

    String getEmail(){
        return this.email;
    }

    void setEmail(String email) {
        this.email = email;
    }

}
