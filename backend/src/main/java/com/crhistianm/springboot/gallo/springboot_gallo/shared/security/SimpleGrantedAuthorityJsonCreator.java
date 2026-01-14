package com.crhistianm.springboot.gallo.springboot_gallo.shared.security;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

abstract class SimpleGrantedAuthorityJsonCreator {

    @JsonCreator
    SimpleGrantedAuthorityJsonCreator(@JsonProperty("authority") String role) {
    }
    
}

