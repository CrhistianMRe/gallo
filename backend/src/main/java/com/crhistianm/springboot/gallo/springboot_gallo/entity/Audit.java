package com.crhistianm.springboot.gallo.springboot_gallo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
//import jakarta.persistence.PreUpdate;

@Embeddable
public class Audit {

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean enabled;

    @PrePersist
    public void PrePersist(){
        this.createdAt = LocalDateTime.now();
        this.enabled = true;
    }

    //@PreUpdate
    //public void PreUpdate(){
        //this.updatedAt= LocalDateTime.now();
    //}

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
     
}

