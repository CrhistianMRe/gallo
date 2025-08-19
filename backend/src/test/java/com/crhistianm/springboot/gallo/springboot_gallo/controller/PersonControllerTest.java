package com.crhistianm.springboot.gallo.springboot_gallo.controller;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.*;

import com.crhistianm.springboot.gallo.springboot_gallo.config.JacksonConfig;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
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
        person.setFirstName(givenPersonRequestDtoOne().orElseThrow().getFirstName());
        person.setLastName(givenPersonRequestDtoOne().orElseThrow().getLastName());
        
        when(personService.save(givenPersonRequestDtoOne().orElseThrow())).thenReturn(person);

        //Given
        mockMvc.perform(post("/api/persons/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(givenPersonRequestDtoOne().orElseThrow())))

            //Then
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.firstName").value("one"))
            .andExpect(jsonPath("$.lastName").value("1one"));
    }

    @Nested
    class ViewModuleTest{

        @BeforeEach
        void setUp(){
            lenient().when(personService.getById(anyLong())).thenAnswer(invo -> {
                Optional<PersonResponseDto> responseOptional = Optional.empty();
                if(invo.getArgument(0, Long.class) == 2L) responseOptional = Optional.of(PersonMapper.entityToResponse(givenPersonEntityTwo().orElseThrow()));
                return responseOptional;
            });

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
            mockMvc.perform(get("/api/persons/2"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.firstName").value("Erick"))
                .andExpect(jsonPath("$.phoneNumber").value("55896144"));
        }
        @Test
        void testViewByIdNotFound() throws Exception {
            mockMvc.perform(get("/api/persons/1"))
                .andExpect(status().isNotFound());
        }

    }

    @Nested
    class UpdateModuleTest{

        @BeforeEach
        void setUp(){
            when(personService.update(anyLong(), any(PersonRequestDto.class))).thenAnswer(invo -> {
                Optional<PersonResponseDto> personResponseOptional = Optional.empty();
                if(invo.getArgument(0, Long.class) == 1L){
                    PersonResponseDto personDto = PersonMapper.entityToResponse(PersonMapper.requestToEntity(invo.getArgument(1)));
                    personDto.setId(invo.getArgument(0, Long.class));
                    personResponseOptional = Optional.of(personDto);
                }
                return personResponseOptional;
            });
        }

        @Test
        void testUpdate() throws Exception {
            mockMvc.perform(put("/api/persons/1")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(givenPersonRequestDtoOne().orElseThrow())))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("one"))
                .andExpect(jsonPath("$.lastName").value("1one"))
                .andExpect(jsonPath("$.gender").value("M"))
                .andExpect(jsonPath("$.phoneNumber").value("123123123"));
        }

        @Test
        void testUpdateNotFound() throws Exception {
            mockMvc.perform(put("/api/persons/2")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(givenPersonRequestDtoTwo().orElseThrow())))
                .andExpect(status().isNotFound());
        }

    }

    @Nested
    class DeleteModuleTest {
        
        @BeforeEach
        void setUp(){
            when(personService.delete(anyLong())).thenAnswer(invo -> {
                Optional<PersonResponseDto> responseOptional = Optional.empty();
                if(invo.getArgument(0, Long.class) == 1L) responseOptional = Optional.of(PersonMapper.entityToResponse(givenPersonEntityOne().orElseThrow()));
                return responseOptional;
            });
        }

        @Test
        void testDelete() throws Exception {
            mockMvc.perform(delete("/api/persons/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Crhistian"))
                .andExpect(jsonPath("$.lastName").value("Mendez"))
                .andExpect(jsonPath("$.phoneNumber").value("4444444"));
        }

        @Test
        void testDeleteNotFound() throws Exception {
            mockMvc.perform(delete("/api/persons/2"))
                .andExpect(status().isNotFound());
        }

    }
}
