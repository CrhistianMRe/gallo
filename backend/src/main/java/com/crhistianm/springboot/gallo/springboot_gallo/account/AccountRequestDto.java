package com.crhistianm.springboot.gallo.springboot_gallo.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//Id not in dto as it is auto incremental on db
class AccountRequestDto extends AbstractAccountRequestDto {

    private boolean admin;

    AccountRequestDto() {
        super(null, null, null);
    }

    AccountRequestDto(String email, String password, Long personId, boolean admin) {
        super(email, password, personId);
        this.admin = admin;
    }

    @Override
    @Email
    @NotBlank
    public String getEmail() {
        return super.getEmail();
    }

    @Override
    @NotNull
    public Long getPersonId() {
        return super.getPersonId();
    }

    @Override
    @NotBlank
    public String getPassword() {
        return super.getPassword();
    }

    boolean isAdmin() {
        return admin;
    }

    void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getPersonId()== null) ? 0 : this.getPersonId().hashCode());
        result = prime * result + ((this.getEmail() == null) ? 0 : this.getEmail().hashCode());
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
        if (this.getPersonId() == null) {
            if (other.getPersonId() != null)
                return false;
        } else if (!this.getPersonId().equals(other.getPersonId()))
            return false;
        if (this.getEmail()== null) {
            if (other.getEmail()!= null)
                return false;
        } else if (!this.getEmail().equals(other.getEmail()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AccountRequestDto [personId=" + this.getPersonId() + ", email=" + this.getEmail() + ", password=" + this.getPassword()+ ", admin="
                + admin + "]";
    }

}
