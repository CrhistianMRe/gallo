package com.crhistianm.springboot.gallo.springboot_gallo.account;

import java.util.ArrayList;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//Without password as password is just for writing
//This will be the response of JWT admin authenticated users
class AccountAdminResponseDto implements AccountResponseDto{

    private Long id;

    private String email;

    //Ignore list of accounts per role
    @JsonIgnoreProperties({"accounts", "handler", "hibernateLazyInitializer"})
    private List<RoleResponseDto> roles;

    private Long personId;

    //As response will show when it was updated/created
    private Audit audit;

    AccountAdminResponseDto() {
        this.roles = new ArrayList<>();
    }

    AccountAdminResponseDto(Long id, String email, Long personId, Audit audit) {
        this();
        this.id = id;
        this.email = email;
        this.personId = personId;
        this.audit = audit;
    }

    Long getId() {
        return id;
    }

    void setId(Long id) {
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

    Audit getAudit() {
        return audit;
    }

    void setAudit(Audit audit) {
        this.audit = audit;
    }

    List<RoleResponseDto> getRoles() {
        return roles;
    }

    void setRoles(List<RoleResponseDto> roles) {
        this.roles = roles;
    }

    void setPersonId(Long personId) {
        this.personId = personId;
    }

    Long getPersonId() {
        return personId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
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
        return "AccountResponseDto [id=" + id + ", email=" + email + "personId=" + personId 
                + ", audit=" + audit + "]";
    }

}
