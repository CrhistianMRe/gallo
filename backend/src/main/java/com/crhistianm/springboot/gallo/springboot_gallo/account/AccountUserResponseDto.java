package com.crhistianm.springboot.gallo.springboot_gallo.account;

class AccountUserResponseDto extends AccountResponseDto{

    AccountUserResponseDto() {
    }

    AccountUserResponseDto(String email, Long personId) {
        this.email = email;
        this.personId = personId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
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
        AccountResponseDto other = (AccountResponseDto) obj;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
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
        return "AccountResponseDto [email=" + email + ", personId=" + personId + "]";
    }

}
