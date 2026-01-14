package com.crhistianm.springboot.gallo.springboot_gallo.shared;

public class FieldInfoError {

    private String name;

    private Class<?> type;

    private Object value;

    private Class<?> ownerClass;

    private String errorMessage;

    public FieldInfoError() {
    }

    public FieldInfoError(String name, Class<?> type, Class<?> ownerClass, Object value, String errorMessage) {
        this.name = name;
        this.type = type;
        this.ownerClass = ownerClass;
        this.value = value;
        this.errorMessage = errorMessage;
    }

    public FieldInfoError(String name, Class<?> type, Class<?> ownerClass, String errorMessage) {
        this.name = name;
        this.type = type;
        this.ownerClass = ownerClass;
        this.errorMessage = errorMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Class<?> getOwnerClass() {
        return ownerClass;
    }

    public void setOwnerClass(Class<?> ownerClass) {
        this.ownerClass = ownerClass;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + ((ownerClass == null) ? 0 : ownerClass.hashCode());
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
        FieldInfoError other = (FieldInfoError) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        if (ownerClass == null) {
            if (other.ownerClass != null)
                return false;
        } else if (!ownerClass.equals(other.ownerClass))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "FieldInfoError [name=" + name + ", type=" + type + ", value=" + value + ", ownerClass=" + ownerClass + "]";
    }
    
}
