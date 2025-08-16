package com.crhistianm.springboot.gallo.springboot_gallo.dto;

import java.util.ArrayList;
import java.util.List;


import com.crhistianm.springboot.gallo.springboot_gallo.entity.Audit;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//Without password as password is just for writing
//This will be the response of JWT admin authenticated users
public class AccountAdminResponseDto implements AccountResponseDto{

    private Long id;

    private String email;

    //Ignore list of accounts per role
    @JsonIgnoreProperties({"accounts", "handler", "hibernateLazyInitializer"})
    private List<RoleResponseDto> roles;

    //Here i add the person to return it as it will be used on administration
    private Person person;

    //As response will show when it was updated/created
    private Audit audit;

    public AccountAdminResponseDto() {
        this.roles = new ArrayList<>();
    }

    public AccountAdminResponseDto(Long id, String email, Person person, Audit audit) {
        this();
        this.id = id;
        this.email = email;
        this.person = person;
        this.audit = audit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }


    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    public List<RoleResponseDto> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleResponseDto> roles) {
        this.roles = roles;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((person == null) ? 0 : person.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AccountAdminResponseDto other = (AccountAdminResponseDto) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (person == null) {
            if (other.person != null)
                return false;
        } else if (!person.equals(other.person))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AccountResponseDto [id=" + id + ", email=" + email + ", person=" + person
                + ", audit=" + audit + "]";
    }

}
