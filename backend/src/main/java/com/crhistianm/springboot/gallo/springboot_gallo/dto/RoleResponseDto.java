package com.crhistianm.springboot.gallo.springboot_gallo.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class RoleResponseDto {

    private Long id;

    private String name;

    //Only display id and email from list of accounts
    @JsonIgnoreProperties({"roles", "audit", "person", "handler", "hibernateLazyInitializer"})
    List<AccountAdminResponseDto> accounts;

    public RoleResponseDto() {
        this.accounts = new ArrayList<>();
    }

    public RoleResponseDto(Long id, String name) {
        this();
        this.id = id;
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAccounts(List<AccountAdminResponseDto> accounts) {
        this.accounts = accounts;
    }

    public List<AccountAdminResponseDto> getAccounts() {
        return accounts;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        RoleResponseDto other = (RoleResponseDto) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RoleResponseDto [id=" + id + ", name=" + name + ", accounts=" + accounts + "]";
    }

}
