package com.crhistianm.springboot.gallo.springboot_gallo.builder;

import java.util.ArrayList;
import java.util.List;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Audit;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Role;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Workout;

public class AccountBuilder {

    private Long id;

    private String email;

    private String password;

    private Audit audit = new Audit();

    private Person person;

    private List<Role> roles;

    private List<Workout> workouts;

    public AccountBuilder(){
        this.roles = new ArrayList<>();
        this.workouts = new ArrayList<>();
    }

    public AccountBuilder id(Long id){
        this.id = id;
        return this;
    }

    public AccountBuilder email(String email){
        this.email = email;
        return this;
    }

    public AccountBuilder password(String password){
        this.password = password;
        return this;
    }

    public AccountBuilder audit(Audit audit){
        this.audit = audit;
        return this;
    }

    public AccountBuilder person(Person person){
        this.person = person;
        return this;
    }
    
    public AccountBuilder roles(List<Role> roles){
        this.roles = roles;
        return this;
    }

    public AccountBuilder workouts(List<Workout> workouts){
        this.workouts = workouts;
        return this;
    }

    public Account build(){
        Account account = new Account();
        account.setId(this.id);
        account.setEmail(this.email);
        account.setPassword(this.password);
        account.setAudit(this.audit);
        account.setPerson(this.person);
        account.setRoles(this.roles);
        account.setWorkouts(this.workouts);
        return account;
    }

}

