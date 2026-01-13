package com.crhistianm.springboot.gallo.springboot_gallo.account;

import java.util.ArrayList;
import java.util.List;

import com.crhistianm.springboot.gallo.springboot_gallo.person.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.workout.Workout;

class AccountBuilder {

    private Long id;

    private String email;

    private String password;

    private Audit audit = new Audit();

    private Person person;

    private List<Role> roles;

    private List<Workout> workouts;

    AccountBuilder(){
        this.roles = new ArrayList<>();
        this.workouts = new ArrayList<>();
    }

    AccountBuilder id(Long id){
        this.id = id;
        return this;
    }

    AccountBuilder email(String email){
        this.email = email;
        return this;
    }

    AccountBuilder password(String password){
        this.password = password;
        return this;
    }

    AccountBuilder audit(Audit audit){
        this.audit = audit;
        return this;
    }

    AccountBuilder person(Person person){
        this.person = person;
        return this;
    }
    
    AccountBuilder roles(List<Role> roles){
        this.roles = roles;
        return this;
    }

    AccountBuilder workouts(List<Workout> workouts){
        this.workouts = workouts;
        return this;
    }

    Account build(){
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

