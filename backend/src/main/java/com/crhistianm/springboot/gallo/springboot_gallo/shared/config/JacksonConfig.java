package com.crhistianm.springboot.gallo.springboot_gallo.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.security.SimpleGrantedAuthorityJsonCreator;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
             //Use JsonCreator to use role instead of authority
            .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();
        //Enable DTO default private deserialization
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

        return mapper;
    }
}
