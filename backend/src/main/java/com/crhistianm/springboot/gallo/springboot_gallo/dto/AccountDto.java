package com.crhistianm.springboot.gallo.springboot_gallo.dto;

//Dto for just receiving person id fk
public class AccountDto {

    private Long id;

    private Long personId;

    private String email;

    private String password;

    private boolean admin;

    public AccountDto() {
    }

    public AccountDto(String email, String password, Long personId, boolean admin) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public String toString() {
        return "AccountDto [id=" + id + ", personId=" + personId + "]";
    }

}
