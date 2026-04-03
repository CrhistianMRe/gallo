package com.crhistianm.springboot.gallo.springboot_gallo.account;

import java.util.List;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.NotEmptyRequest;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.group.FirstCheck;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.group.SecondCheck;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;


@NotEmptyRequest(groups = FirstCheck.class, hasSuper = true)
final class AccountUpdateRequestDto extends AbstractAccountRequestDto {

    private final Boolean enabled;

    private final List<RoleRequestDto> roles;

    @JsonCreator
    AccountUpdateRequestDto
    (
     @JsonProperty(value = "email") String email,
     @JsonProperty(value = "password") String password,
     @JsonProperty(value = "enabled") Boolean enabled,
     @JsonProperty(value = "roles") List<RoleRequestDto> roles,
     @JsonProperty(value = "personId") Long personId
     ) {
        super(email, password, personId);
        this.roles = roles == null ? List.of() : List.copyOf(roles);
        this.enabled = enabled;
     }

    @Override
    @Email(groups = SecondCheck.class)
    public String getEmail() {
        return super.getEmail();
    }

    @Override
    @Size(min = 4, groups = SecondCheck.class)
    public String getPassword() {
        return super.getPassword();
    }

    Boolean isEnabled() {
        return enabled;
    }

    List<RoleRequestDto> getRoles() {
        return roles;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getEmail() == null) ? 0 : this.getEmail().hashCode());
        result = prime * result + ((this.getPassword() == null) ? 0 : this.getPassword().hashCode());
        result = prime * result + ((this.enabled == null) ? 0 : enabled.hashCode());
        result = prime * result + ((this.getPersonId() == null) ? 0 : this.getPersonId().hashCode());
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
        if (this.getEmail() == null) {
            if (other.getEmail() != null)
                return false;
        } else if (!this.getEmail().equals(other.getEmail()))
            return false;
        if (getPassword() == null) {
            if (other.getPassword() != null)
                return false;
        } else if (!this.getPassword().equals(other.getPassword()))
            return false;
        if (enabled == null) {
            if (other.enabled != null)
                return false;
        } else if (!enabled.equals(other.enabled))
            return false;
        if (this.getPersonId() == null) {
            if (other.getPersonId()!= null)
                return false;
        } else if (!this.getPersonId().equals(other.getPersonId()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AccountUpdateRequestDto [email=" + this.getEmail() + ", password=" + this.getPassword() + ", enabled="
                + enabled + ", roles=" + roles + ", personId=" + this.getPersonId() + "]";
    }

}

