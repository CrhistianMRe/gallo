package com.crhistianm.springboot.gallo.springboot_gallo.person;

import static org.mockito.Mockito.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static com.crhistianm.springboot.gallo.springboot_gallo.person.PersonData.*;

import com.crhistianm.springboot.gallo.springboot_gallo.config.ValidatorConfig;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.config.JacksonConfig;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.NotFoundException;
import com.crhistianm.springboot.gallo.springboot_gallo.account.AccountUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;

//Select controller class
@WebMvcTest(PersonController.class)
//Import security filterChain to expose endpoints and JacksonMapper config
@Import({JacksonConfig.class})
//remove security filters as is not needed in these tests
@AutoConfigureMockMvc(addFilters = false)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonService personService;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AccountUserDetailsService service;

    @Nested
    @Import(ValidatorConfig.class)
    class RegisterModuleTest {

        PersonRequestDto personRequest;

        @BeforeEach
        void setUp () {
            //All valid fields
            personRequest = new PersonRequestDto();
            personRequest.setFirstName("exampleFirst");
            personRequest.setLastName("exampleLast");
            personRequest.setPhoneNumber("12345678");
            personRequest.setBirthDate(LocalDate.of(2010, 02, 23));
            personRequest.setGender("M");
            personRequest.setHeight(1.60);
            personRequest.setWeight(60.00);

            doAnswer(invo -> {
                PersonResponseDto personResponseDto = PersonMapper.entityToResponse(PersonMapper.requestToEntity(invo.getArgument(0, PersonRequestDto.class)));
                personResponseDto.setId(1L);
                return personResponseDto;
            }).when(personService).save(any(PersonRequestDto.class));
        }

        @Test
        @DisplayName("Testing post person into endpoint")
        void testCreate() throws Exception {

            //Given
            mockMvc.perform(post("/api/persons/register")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(personRequest)))

                //Then
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("exampleFirst"))
                .andExpect(jsonPath("$.lastName").value("exampleLast"))
                .andExpect(jsonPath("$.phoneNumber").value("12345678"))
                .andExpect(jsonPath("$.birthDate").value(Matchers.contains(2010, 02, 23)))
                .andExpect(jsonPath("$.gender").value("M"))
                .andExpect(jsonPath("$.height").value(1.60))
                .andExpect(jsonPath("$.weight").value(60.00));
        }

        @Test
        void shouldReturnErrorWhenDtoFieldIsInvalid() throws Exception {
            personRequest.setPhoneNumber("");

            mockMvc.perform(request(HttpMethod.POST, "/api/persons/register")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(personRequest)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.phoneNumber").value("the field phoneNumber must not be blank"));
        }

    }

    @Nested
    class ViewModuleTest{

        @BeforeEach
        void setUp(){
            lenient().when(personService.getById(anyLong())).thenAnswer(invo -> {
                if(invo.getArgument(0, Long.class) != 2L) throw new NotFoundException(Person.class);
                return PersonMapper.entityToResponse(givenPersonEntityTwo().orElseThrow());
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
                .andExpect(jsonPath("$.message").value("Person not found"))
                .andExpect(status().isNotFound());
        }

    }

    @Nested
    @Import(ValidatorConfig.class)
    class UpdateModuleTest{

        PersonRequestDto personRequest;

        @BeforeEach
        void setUp(){

            personRequest = new PersonRequestDto();
            personRequest.setFirstName("exampleFirst");
            personRequest.setLastName("exampleLast");
            personRequest.setPhoneNumber("12345678");
            personRequest.setBirthDate(LocalDate.of(2010, 02, 23));
            personRequest.setGender("M");
            personRequest.setHeight(1.60);
            personRequest.setWeight(60.00);

            when(personService.update(anyLong(), any(PersonRequestDto.class))).thenAnswer(invo -> {
                if(invo.getArgument(0, Long.class) != 1L) throw new NotFoundException(Person.class);
                PersonResponseDto personDto = PersonMapper.entityToResponse(PersonMapper.requestToEntity(invo.getArgument(1)));
                personDto.setId(invo.getArgument(0, Long.class));
                return personDto;
            });
        }

        @Test
        void testUpdate() throws Exception {
            mockMvc.perform(put("/api/persons/1")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(personRequest)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("exampleFirst"))
                .andExpect(jsonPath("$.lastName").value("exampleLast"))
                .andExpect(jsonPath("$.phoneNumber").value("12345678"))
                .andExpect(jsonPath("$.birthDate").value(Matchers.contains(2010, 02, 23)))
                .andExpect(jsonPath("$.gender").value("M"))
                .andExpect(jsonPath("$.height").value(1.60))
                .andExpect(jsonPath("$.weight").value(60.00));
        }

        @Test
        void testUpdateNotFound() throws Exception {
            mockMvc.perform(put("/api/persons/2")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(personRequest)))
                .andExpect(jsonPath("$.message").value("Person not found"))
                .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnErrorWhenDtoFieldIsInvalid() throws Exception {
            personRequest.setPhoneNumber("");
            mockMvc.perform(put("/api/persons/1")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(personRequest)))
                .andExpect(jsonPath("$.phoneNumber").value("the field phoneNumber must not be blank"))
                .andExpect(status().isBadRequest());
        }

    }

    @Nested
    class DeleteModuleTest {
        
        @BeforeEach
        void setUp(){
            when(personService.delete(anyLong())).thenAnswer(invo -> {
                if(invo.getArgument(0, Long.class) != 1L) throw new NotFoundException(Person.class);
                return PersonMapper.entityToResponse(givenPersonEntityOne().orElseThrow());
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
                .andExpect(jsonPath("$.message").value("Person not found"))
                .andExpect(status().isNotFound());
        }

    }
}
