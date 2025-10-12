package com.crhistianm.springboot.gallo.springboot_gallo.dto;

public abstract class AbstractAccountRequestDto {

    private String email;

    private String password;

    private Long personId;

    public AbstractAccountRequestDto(String email, String password, Long personId) {
        this.email = email;
        this.password = password;
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

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract String toString();


    








    
}
