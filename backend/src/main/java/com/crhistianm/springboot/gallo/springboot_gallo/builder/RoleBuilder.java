package com.crhistianm.springboot.gallo.springboot_gallo.builder;

import java.util.List;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Role;

public class RoleBuilder {

    private Long id;

    private String name;

    private List<Account> accounts;

    public RoleBuilder id(Long id){
        this.id = id;
        return this;
    }

    public RoleBuilder name(String name){
        this.name = name;
        return this;
    }

    public RoleBuilder accounts(List<Account> accounts){
        this.accounts = accounts;
        return this;
    }

    public Role build(){
        Role role = new Role();
        role.setId(this.id);
        role.setName(this.name);
        role.setAccounts(this.accounts);
        return role;
    }
    
}
