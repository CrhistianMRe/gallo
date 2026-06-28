package com.crhistianm.springboot.gallo.springboot_gallo.account;

import static com.crhistianm.springboot.gallo.springboot_gallo.shared.cache.CacheModule.ACCOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
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
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

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
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class AccountServiceCacheTest {

    @Container
    @ServiceConnection
    private static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
        .withExposedPorts(6379);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CacheManager cacheManager;

    @MockitoSpyBean
    private AccountRepository spyAccountRepository;

    @Autowired
    private AccountRepository accountRepository;

    @MockitoBean
    private AccountValidator accountValidator;

    @MockitoBean
    private IdentityVerificationService identityVerificationService;

    @AfterEach
    void doAfter() {
        accountRepository.deleteAll();
        cacheManager.getCache(ACCOUNT).clear();
    }

    @Test
    @Sql(value = {"/personinserts.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldCacheResponseWhenClientIsUserAuthorityAndAccountIsCreated() throws Exception {
        final String email = "testemail@gmail.com";
        final String password = "12345";
        final Long personId = 1L;
        final boolean admin = false;
        AccountRequestDto requestDto = new AccountRequestDto(email, password, personId, admin);

        doReturn(false).when(identityVerificationService).isAdminAuthority();

        MvcResult result = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andReturn();

        AccountUserResponseDto expectedResponseDto = objectMapper.readValue(result.getResponse().getContentAsString(), AccountUserResponseDto.class);

        Cache cache = cacheManager.getCache(ACCOUNT);

        assertThat(cache).isNotNull();

        //Retrieving from database as response dto does not contain account id
        final Long savedAccountId = accountRepository.findAll().iterator().next().getId();

        final AccountUserResponseDto cachedResponse = cache.get(savedAccountId, AccountUserResponseDto.class);

        assertThat(cachedResponse).isNotNull();
        assertThat(cachedResponse).isEqualTo(expectedResponseDto);
    }

    @Test
    @Sql(value = {"/personinserts.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldNotCacheResponseWhenClientIsAdminAuthorityAndAccountIsCreated() throws Exception {
        final String email = "testemail@gmail.com";
        final String password = "12345";
        final Long personId = 1L;
        final boolean admin = false;
        AccountRequestDto requestDto = new AccountRequestDto(email, password, personId, admin);

        doReturn(true).when(identityVerificationService).isAdminAuthority();

        MvcResult result = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andReturn();

        final Long responseId = objectMapper.readValue(result.getResponse().getContentAsString(), AccountAdminResponseDto.class).getId();

        assertThat(responseId).isNotNull();

        Cache cache = cacheManager.getCache(ACCOUNT);

        final AccountResponseDto cachedResponse = cache.get(responseId, AccountResponseDto.class);

        assertThat(cachedResponse).isNull();
    }

    @Test
    @Sql(value = {"/personinserts.sql", "/accountinserts.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldCacheResponseWhenClientIsUserAuthorityAndIsUpdated() throws Exception {
        doReturn(false).when(identityVerificationService).isAdminAuthority();

        final Long accountId = 1L;
        final String email = "updatedcrhistian@gmail.com";
        final String password = "123456";

        //All of this fields are admin's, which means they are not cached so all are null/empty
        final List<RoleRequestDto> roles = new ArrayList<>();
        final Boolean enabled = null;
        final Long personId = null;

        AccountUpdateRequestDto requestDto = new AccountUpdateRequestDto(email, password, enabled, roles, personId);

        mockMvc.perform(patch("/api/accounts/" + accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk());

        Cache cache = cacheManager.getCache(ACCOUNT);

        assertThat(cache).isNotNull();

        AccountUserResponseDto cachedResponse = cache.get(accountId, AccountUserResponseDto.class);

        assertThat(cachedResponse).isNotNull();
    }

    @Test
    @Sql(value = {"/personinserts.sql", "/accountinserts.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldNotCacheResponseWhenClientIsAdminAuthorityAndIsUpdated() throws Exception { 
        doReturn(true).when(identityVerificationService).isAdminAuthority();

        final Long accountId = 1L;
        final String email = "updatedcrhistian@gmail.com";
        final String password = "123456";

        //All of this fields are admin's, which means they are not cached so all are null/empty
        final List<RoleRequestDto> roles = new ArrayList<>();
        final Boolean enabled = null;
        final Long personId = null;

        AccountUpdateRequestDto requestDto = new AccountUpdateRequestDto(email, password, enabled, roles, personId);

        mockMvc.perform(patch("/api/accounts/" + accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk());

        Cache cache = cacheManager.getCache(ACCOUNT);

        AccountResponseDto cachedResponse = cache.get(accountId, AccountResponseDto.class);

        assertThat(cachedResponse).isNull();
    }

    @Test
    @Sql(value = {"/personinserts.sql", "/accountinserts.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldRemoveCachedResponseWhenAccountIsDeleted() throws Exception {
        final Long accountId = 1L;

        final String email = "testemail@gmail.com";
        final Long personId = 2L;

        AccountUserResponseDto responseDtoToCache = new AccountUserResponseDto(email, personId);
            
        cacheManager.getCache(ACCOUNT).put(accountId, responseDtoToCache);

        mockMvc.perform(delete("/api/accounts/" + accountId))
            .andExpect(status().isOk());

        AccountUserResponseDto cachedResponse = cacheManager.getCache(ACCOUNT).get(accountId, AccountUserResponseDto.class);

        assertThat(cachedResponse).isNull();
    }

    @Test
    @Sql(value = {"/personinserts.sql", "/accountinserts.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldCacheResponseWhenIsRequestedByIdAndClientIsUserAuthority() throws Exception {
        doReturn(false).when(identityVerificationService).isAdminAuthority();

        final Long accountId = 1L;

        mockMvc.perform(get("/api/accounts/" + accountId))
            .andExpect(status().isOk());

        Cache cache = cacheManager.getCache(ACCOUNT);

        AccountUserResponseDto cachedResponse = cache.get(accountId, AccountUserResponseDto.class);

        assertThat(cachedResponse).isNotNull();

        MvcResult result = mockMvc.perform(get("/api/accounts/" + accountId))
            .andExpect(status().isOk())
            .andReturn();

        AccountUserResponseDto expectedResponseDto = objectMapper.readValue(result.getResponse().getContentAsString(), AccountUserResponseDto.class);

        assertThat(expectedResponseDto).isEqualTo(cachedResponse);

        verify(spyAccountRepository, times(2)).existsById(eq(accountId));
        verify(identityVerificationService, times(2)).isAdminAuthority();
        verify(spyAccountRepository, times(1)).findById(eq(accountId));
        verifyNoMoreInteractions(spyAccountRepository);
    }

    @Test
    @Sql(value = {"/personinserts.sql", "/accountinserts.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldNotCacheResponseWhenClientIsAdminAuthority() throws Exception{
        doReturn(true).when(identityVerificationService).isAdminAuthority();

        final Long accountId = 1L;

        mockMvc.perform(get("/api/accounts/" + accountId))
            .andExpect(status().isOk());

        Cache cache = cacheManager.getCache(ACCOUNT);

        AccountAdminResponseDto cachedResponse = cache.get(accountId, AccountAdminResponseDto.class);

        assertThat(cachedResponse).isNull();

        mockMvc.perform(get("/api/accounts/" + accountId))
            .andExpect(status().isOk());

        verify(spyAccountRepository, times(2)).existsById(eq(accountId));
        verify(identityVerificationService, times(2)).isAdminAuthority();
        verify(spyAccountRepository, times(2)).findById(eq(accountId));
        verifyNoMoreInteractions(spyAccountRepository);
    }


}
