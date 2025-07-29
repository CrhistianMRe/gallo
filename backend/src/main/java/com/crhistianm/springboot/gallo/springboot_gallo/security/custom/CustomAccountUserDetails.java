package com.crhistianm.springboot.gallo.springboot_gallo.security.custom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomAccountUserDetails implements UserDetails{

    private String password;

    private String email;

    private String username;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;
    
    private boolean enabled;

    private List<GrantedAuthority> authorities;

    public CustomAccountUserDetails() {
        this.authorities = new ArrayList<>();
    }

    public CustomAccountUserDetails(String password, String email, String username, boolean accountNonExpired,
            boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled,
            List<GrantedAuthority> authorities) {
        this.password = password;
        this.email = email;
        this.username = username;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public void eraseCredentials(){
        this.password = null;
        this.username = null;
    }


}

