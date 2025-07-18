package com.crhistianm.springboot.gallo.springboot_gallo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonConfig {


    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

        return mapper;
    }
}
