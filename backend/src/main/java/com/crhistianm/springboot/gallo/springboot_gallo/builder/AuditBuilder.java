package com.crhistianm.springboot.gallo.springboot_gallo.builder;

import java.time.LocalDateTime;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.Audit;

public class AuditBuilder {

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    private boolean enabled;

    public AuditBuilder(){}

    public AuditBuilder createdAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
        return this;
    }
    
    public AuditBuilder updatedAt(LocalDateTime updatedAt){
        this.updatedAt = updatedAt;
        return this;
    }

    public AuditBuilder enabled(boolean enabled){
        this.enabled = enabled;
        return this;
    }

    public Audit build(){
        Audit audit = new Audit();
        audit.setCreatedAt(this.createdAt);
        audit.setUpdatedAt(this.updatedAt);
        audit.setEnabled(this.enabled);
        return audit;
    }

}

