package com.crhistianm.springboot.gallo.springboot_gallo.account;

import java.util.List;

class RoleBuilder {

    private Long id;

    private String name;

    private List<Account> accounts;

    RoleBuilder id(Long id){
        this.id = id;
        return this;
    }

    RoleBuilder name(String name){
        this.name = name;
        return this;
    }

    RoleBuilder accounts(List<Account> accounts){
        this.accounts = accounts;
        return this;
    }

    Role build(){
        Role role = new Role();
        role.setId(this.id);
        role.setName(this.name);
        role.setAccounts(this.accounts);
        return role;
    }
    
}
