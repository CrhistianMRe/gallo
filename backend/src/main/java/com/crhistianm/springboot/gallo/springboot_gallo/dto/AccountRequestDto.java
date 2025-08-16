package com.crhistianm.springboot.gallo.springboot_gallo.dto;


import com.crhistianm.springboot.gallo.springboot_gallo.validation.AdminRequired;
import com.crhistianm.springboot.gallo.springboot_gallo.validation.PersonNotAssigned;
import com.crhistianm.springboot.gallo.springboot_gallo.validation.PersonRegistered;
import com.crhistianm.springboot.gallo.springboot_gallo.validation.UniqueEmail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//Id not in dto as it is auto incremental on db
public class AccountRequestDto{

    @NotNull
    @PersonNotAssigned
    @PersonRegistered
    private Long personId;

    @NotBlank
    @UniqueEmail
    @Email
    private String email;

    @NotBlank
    private String password;

    @AdminRequired
    private boolean admin;

    public AccountRequestDto() {
    }

    public AccountRequestDto(String email, String password, Long personId, boolean admin) {
        this.email = email;
        this.password = password;
        this.admin = admin;
        this.personId = personId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((personId == null) ? 0 : personId.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
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
        AccountRequestDto other = (AccountRequestDto) obj;
        if (personId == null) {
            if (other.personId != null)
                return false;
        } else if (!personId.equals(other.personId))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AccountRequestDto [personId=" + personId + ", email=" + email + ", password=" + password + ", admin="
                + admin + "]";
    }

}
