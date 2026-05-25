package com.crhistianm.springboot.gallo.springboot_gallo.person;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.crhistianm.springboot.gallo.springboot_gallo.account.IdentityVerificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

//Enable H2 
@TestPropertySource(properties = {
"spring.datasource.url=jdbc:h2:mem:testdb",
"spring.datasource.driverClassName=org.h2.Driver",
"spring.datasource.username=sa",
"spring.datasource.password=password",
"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
"spring.docker.compose.enabled=false"
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class PersonServiceCacheTest {

    @Container
    @ServiceConnection
    private static GenericContainer<?> redis = new GenericContainer(DockerImageName.parse("redis:7.4-alpine"))
        .withExposedPorts(6379);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    //This is only used for using verify with Mockito. But both are the same bean
    @MockitoSpyBean
    private PersonRepository personRepositorySpy;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private PersonValidator personValidator;

    @MockitoBean
    private IdentityVerificationService identityVerificationService;

    @AfterEach
    void setUp() {
        personRepository.deleteAll();
        cacheManager.getCache("PERSON").clear();
    }

    @Test
    void shouldCacheResponseWhenPersonIsCreated() throws Exception {  
        final String firstName = "testname";
        final String lastName = "testlastname";
        final String phoneNumber = "992222";
        final LocalDate birthDate = LocalDate.of(2010, 9, 27);
        final String gender = "M";

        final Double height = null;
        final Double weight = null;


        PersonRequestDto requestDto = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

        MvcResult result = mockMvc.perform(post("/api/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andReturn();

        PersonResponseDto expectedResponse = objectMapper.readValue(result.getResponse().getContentAsString(), PersonResponseDto.class);

        Long savedPersonId = expectedResponse.getId();

        Cache cache = cacheManager.getCache("PERSON");

        assertThat(cache).isNotNull();

        PersonResponseDto cachedResponseDto = cache.get(savedPersonId, PersonResponseDto.class);

        assertThat(cachedResponseDto).isNotNull();
        assertThat(cachedResponseDto).isEqualTo(expectedResponse);

    }

    @Test 
    @Sql(value = {"/personinserts.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldCacheResponseWhenPersonIsUpdated() throws Exception {

        final Long personId = 1L;

        final String firstName = "Crhistian";
        final String lastName = "modified";
        final String phoneNumber = "111222333";
        final LocalDate birthDate = LocalDate.of(2000, 1, 01);
        final String gender = "M";

        final Double height = 1.74;
        final Double weight = 80.0;

        PersonRequestDto requestDto = new PersonRequestDto(firstName, lastName, phoneNumber, birthDate, gender, height, weight);

        mockMvc.perform(put("/api/persons/" + personId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andReturn();

        Cache cache = cacheManager.getCache("PERSON");

        assertThat(cache).isNotNull();

        PersonResponseDto responseDto = cache.get(personId, PersonResponseDto.class);

        assertThat(responseDto).isNotNull();
    }

    @Test
    @Sql(value = {"/personinserts.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldCacheResponseWhenPersonIsRequested() throws Exception {
        final Long personId = 1L;

        mockMvc.perform(get("/api/persons/" + personId))
            .andExpect(status().isOk());

        Cache cache = cacheManager.getCache("PERSON");

        PersonResponseDto cachedResponse = cache.get(personId, PersonResponseDto.class);

        assertThat(cachedResponse).isNotNull();

        MvcResult result =  mockMvc.perform(get("/api/persons/" + personId))
            .andExpect(status().isOk())
            .andReturn();

        PersonResponseDto requestResponse = objectMapper
            .readValue(result.getResponse().getContentAsString(), PersonResponseDto.class);

        assertThat(cachedResponse).isEqualTo(requestResponse);

        verify(personRepositorySpy, times(2)).existsById(eq(personId));
        verify(identityVerificationService, times(2)).validateUserAllowanceByPersonId(eq(personId));
        verify(personRepositorySpy, times(1)).findById(eq(personId));
        verifyNoMoreInteractions(personRepositorySpy);
    }

    @Test
    @Sql(value = {"/personinserts.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldRemoveCachedResponseWhenPersonIsDeleted() throws Exception {
        final Long personId = 1L;

        mockMvc.perform(get("/api/persons/" + personId))
            .andExpect(status().isOk());

        PersonResponseDto cachedResponse = cacheManager.getCache("PERSON").get(personId, PersonResponseDto.class);

        assertThat(cachedResponse).isNotNull();

        mockMvc.perform(delete("/api/persons/" + personId))
            .andExpect(status().isOk());

        cachedResponse = cacheManager.getCache("PERSON").get(personId, PersonResponseDto.class);

        assertThat(cachedResponse).isNull();
    }

}
