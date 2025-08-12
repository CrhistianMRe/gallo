package com.crhistianm.springboot.gallo.springboot_gallo.controller;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.*;

import com.crhistianm.springboot.gallo.springboot_gallo.config.JacksonConfig;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.PersonMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.security.SpringSecurityConfig;
import com.crhistianm.springboot.gallo.springboot_gallo.service.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Validator;

//Select controller class
@WebMvcTest(PersonController.class)
//Import security filterChain to expose endpoints and JacksonMapper config
@Import({SpringSecurityConfig.class, JacksonConfig.class})
//remove security filters as is not needed in these tests
@AutoConfigureMockMvc(addFilters = false)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonService personService;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    Validator validator;

    @Test
    @DisplayName("Testing post person into endpoint")
    void testCreate() throws Exception {
        
        PersonResponseDto person = new PersonResponseDto();
        person.setId(1L);
        person.setFirstName(givenPersonCreateDtoOne().orElseThrow().getFirstName());
        person.setLastName(givenPersonCreateDtoOne().orElseThrow().getLastName());
        
        when(personService.save(givenPersonCreateDtoOne().orElseThrow())).thenReturn(person);

        //Given
        mockMvc.perform(post("/api/persons/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(givenPersonCreateDtoOne().orElseThrow())))

            //Then
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.firstName").value("one"))
            .andExpect(jsonPath("$.lastName").value("1one"));
    }

    @Test
    void testViewAll() throws Exception {
        when(personService.getAll()).thenReturn(List.of(PersonMapper.entityToResponse(givenPersonEntityOne().orElseThrow()), PersonMapper.entityToResponse(givenPersonEntityTwo().orElseThrow())));

        mockMvc.perform(get("/api/persons"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[0].firstName").value("Crhistian"))
            .andExpect(jsonPath("$[1].firstName").value("Erick"))
            .andExpect(jsonPath("$[1].phoneNumber").value("55896144"))
            .andExpect(jsonPath("$[0].phoneNumber").value("4444444"));
    }

    @Test
    void testViewById() throws Exception {
        when(personService.getById(2L)).thenReturn(PersonMapper.entityToResponse(givenPersonEntityTwo().orElseThrow()));

        mockMvc.perform(get("/api/persons/2"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(2L))
            .andExpect(jsonPath("$.firstName").value("Erick"))
            .andExpect(jsonPath("$.phoneNumber").value("55896144"));
    }

}
