package com.crhistianm.springboot.gallo.springboot_gallo.exercise;

import static com.crhistianm.springboot.gallo.springboot_gallo.shared.cache.CacheModule.EXERCISE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ExerciseServiceCacheTest {


    @Container
    @ServiceConnection
    private static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
        .withExposedPorts(6379);

    @Autowired
    private MockMvc mockMvc ;

    @MockitoSpyBean
    private ExerciseRepository spyExerciseRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        cacheManager.getCache(EXERCISE).clear();
        exerciseRepository.deleteAll();
    }

    @Test
    @Sql(value = {"/exerciseinserts.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldCacheGetAllExerciseList() throws Exception {

        MvcResult result = mockMvc.perform(get("/api/exercises"))
            .andExpect(status().isOk())
            .andReturn();

        List<ExerciseResponseDto> expectedResultList = objectMapper
            .readValue(result.getResponse().getContentAsString(), new TypeReference<List<ExerciseResponseDto>>(){});

        String cachedResponseString = cacheManager.getCache(EXERCISE).get("getAll").get().toString();

        List<ExerciseResponseDto> expectedCachedString = objectMapper
            .readValue(cachedResponseString, objectMapper.getTypeFactory().constructCollectionType(List.class, ExerciseResponseDto.class));

        assertThat(expectedResultList).isEqualTo(expectedCachedString);

        verify(spyExerciseRepository, times(1)).findAll();
    }
    
}
