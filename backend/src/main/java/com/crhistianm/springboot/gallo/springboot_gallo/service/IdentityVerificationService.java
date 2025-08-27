package com.crhistianm.springboot.gallo.springboot_gallo.service;

public interface IdentityVerificationService {

    boolean isAdminAuthority();

    boolean isUserPersonEntityAllowed(Long id);

}
