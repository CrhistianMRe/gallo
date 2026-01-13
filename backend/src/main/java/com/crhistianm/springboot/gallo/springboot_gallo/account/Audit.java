package com.crhistianm.springboot.gallo.springboot_gallo.account;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Embeddable
class Audit {

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean enabled;

    @PrePersist
    private void PrePersist(){
        this.createdAt = LocalDateTime.now();
        this.enabled = true;
    }

    @PreUpdate
    private void PreUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    LocalDateTime getCreatedAt() {
        return createdAt;
    }

    void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    boolean isEnabled() {
        return enabled;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
     
}

