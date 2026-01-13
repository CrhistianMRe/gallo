package com.crhistianm.springboot.gallo.springboot_gallo.account;

import java.time.LocalDateTime;

class AuditBuilder {

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    private boolean enabled;

    AuditBuilder(){}

    AuditBuilder createdAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
        return this;
    }
    
    AuditBuilder updatedAt(LocalDateTime updatedAt){
        this.updatedAt = updatedAt;
        return this;
    }

    AuditBuilder enabled(boolean enabled){
        this.enabled = enabled;
        return this;
    }

    Audit build(){
        Audit audit = new Audit();
        audit.setCreatedAt(this.createdAt);
        audit.setUpdatedAt(this.updatedAt);
        audit.setEnabled(this.enabled);
        return audit;
    }

}

