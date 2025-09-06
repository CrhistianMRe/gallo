package com.crhistianm.springboot.gallo.springboot_gallo.dto;

import java.util.ArrayList;
import java.util.List;

public class AccountUpdateRequestDto {

    private String email;

    private String password;

    private Boolean enabled;

    private List<RoleRequestDto> roles;
    
    private Long personId;

    public AccountUpdateRequestDto() {
        this.roles = new ArrayList<>();
    }

    public AccountUpdateRequestDto(String email, String password, Boolean enabled, List<RoleRequestDto> roles, Long personId) {
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.roles = roles;
        this.personId = personId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public List<RoleRequestDto> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleRequestDto> roles) {
        this.roles = roles;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
        result = prime * result + ((roles == null) ? 0 : roles.hashCode());
        result = prime * result + ((personId == null) ? 0 : personId.hashCode());
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
        AccountUpdateRequestDto other = (AccountUpdateRequestDto) obj;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (enabled == null) {
            if (other.enabled != null)
                return false;
        } else if (!enabled.equals(other.enabled))
            return false;
        if (roles == null) {
            if (other.roles != null)
                return false;
        } else if (!roles.equals(other.roles))
            return false;
        if (personId == null) {
            if (other.personId != null)
                return false;
        } else if (!personId.equals(other.personId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AccountUpdateRequestDto [email=" + email + ", password=" + password + ", enabled=" + enabled
                + ", roles=" + roles + ", personId=" + personId + "]";
    }



}

