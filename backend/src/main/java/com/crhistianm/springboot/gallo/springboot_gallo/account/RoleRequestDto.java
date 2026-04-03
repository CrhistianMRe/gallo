package com.crhistianm.springboot.gallo.springboot_gallo.account;


import com.crhistianm.springboot.gallo.springboot_gallo.shared.RequestDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final class RoleRequestDto implements RequestDto {

    private final Long id;

    private final String name;

    @JsonCreator
    RoleRequestDto
    (
     @JsonProperty(value = "id") Long id, 
     @JsonProperty(value = "name") String name
     ) {
        this.id = id;
        this.name = name;
     }

    Long getId() {
        return id;
    }

    String getName() {
        return name;
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
        RoleRequestDto other = (RoleRequestDto) obj;
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
        return "RoleRequestDto [id=" + id + ", name=" + name + "]";
    }

}

