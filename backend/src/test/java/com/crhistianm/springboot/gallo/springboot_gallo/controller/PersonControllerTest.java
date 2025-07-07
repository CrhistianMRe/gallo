package com.crhistianm.springboot.gallo.springboot_gallo.controller;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.*;

import com.crhistianm.springboot.gallo.springboot_gallo.security.SpringSecurityConfig;
import com.crhistianm.springboot.gallo.springboot_gallo.service.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;

//Select controller class
@WebMvcTest(PersonController.class)
//Import security filterChain to expose endpoints
@Import(SpringSecurityConfig.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonService personService;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Testing post person into endpoint")
    void testCreate() throws Exception {
        when(personService.save(createPerson().orElseThrow())).thenReturn(createPerson().orElseThrow());

        //Given
        mockMvc.perform(post("/api/persons/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createPerson().orElseThrow())))

            //Then
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.firstName").value("ejemplo"));
    }
    
}
