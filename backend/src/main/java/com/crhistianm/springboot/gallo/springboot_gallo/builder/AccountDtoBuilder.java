package com.crhistianm.springboot.gallo.springboot_gallo.builder;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.AccountDto;

public class AccountDtoBuilder {

    private Long id;

    private Long personId;

    private String email;

    private String password;

    private boolean admin;

    public AccountDtoBuilder (){}

    public AccountDtoBuilder id(Long id){
        this.id = id;
        return this;
    }

    public AccountDtoBuilder personId(Long personId){
        this.personId = personId;
        return this;
    }

    public AccountDtoBuilder email(String email){
        this.email = email;
        return this;
    }

    public AccountDtoBuilder password(String password){
        this.password = password;
        return this;
    }

    public AccountDtoBuilder admin(boolean admin){
        this.admin = admin;
        return this;
    }

    public AccountDto build(){
        AccountDto accountDto = new AccountDto();
        accountDto.setId(this.id);
        accountDto.setPersonId(this.personId);
        accountDto.setEmail(this.email);
        accountDto.setPassword(this.password);
        accountDto.setAdmin(this.admin);
        return accountDto;
    }
    
}
