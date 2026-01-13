package com.crhistianm.springboot.gallo.springboot_gallo.account;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static com.crhistianm.springboot.gallo.springboot_gallo.account.AccountData.*;
import static com.crhistianm.springboot.gallo.springboot_gallo.person.PersonData.getPersonInstance;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.config.JacksonConfig;
import com.crhistianm.springboot.gallo.springboot_gallo.person.Person;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AccountController.class)
@Import({JacksonConfig.class})
//remove security filters as is not needed in these tests
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService; 

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AccountUserDetailsService service;


    @Nested
    class RegisterModuleTest {

        AccountRequestDto requestDto;

        @BeforeEach
        void setUp() {
            requestDto = new AccountRequestDto();
            requestDto.setEmail("email@gmail.com");
            requestDto.setPassword("12345");
            requestDto.setPersonId(1L);
            doAnswer(invo ->{
                Account account = AccountMapper.requestToEntity(invo.getArgument(0, AccountRequestDto.class));
                Person person = getPersonInstance();
                person.setId(1L);
                account.setPerson(person);
                return AccountMapper.entityToAdminResponse(account);
            }).when(accountService).save(any(AccountRequestDto.class));
        }

        @Test
        void testCreate() throws Exception{
            //Given
            mockMvc.perform(post("/api/accounts/register")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)))
                //Then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("email@gmail.com"))
                .andExpect(jsonPath("$.personId").value(1L));
        }

        @Test
        void shouldReturnFieldMessageWhenFieldIsInvalid() throws Exception {
            requestDto.setPassword("");
            mockMvc.perform(request(HttpMethod.POST, "/api/accounts/register")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("the field password must not be blank"));
        }

    }


    @Nested
    class ViewModuleTest {

        @BeforeEach
        void setUp(){
            when(accountService.getById(anyLong())).thenAnswer(invo ->{
                if(invo.getArgument(0, Long.class) == 99L) throw new NotFoundException(Account.class);
                if(invo.getArgument(0, Long.class) == 1L) return AccountMapper.entityToAdminResponse(givenAccountEntityAdmin().orElseThrow());
                return AccountMapper.entityToResponse(givenAccountEntityUser().orElseThrow());
            });
        }

        @Test
        void testViewAll() throws Exception {
            when(accountService.getAll()).thenReturn(List.of((AccountAdminResponseDto)AccountMapper.entityToAdminResponse(givenAccountEntityAdmin().orElseThrow()), 
                    (AccountAdminResponseDto)AccountMapper.entityToAdminResponse(givenAccountEntityUser().orElseThrow())));
            mockMvc.perform(get("/api/accounts"))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("admin@gmail.com"))
                .andExpect(jsonPath("$[1].email").value("user@gmail.com"));
        }

        @Test
        void testViewByIdAdmin() throws Exception {
            mockMvc.perform(get("/api/accounts/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("admin@gmail.com"));
        }

        @Test
        void testViewByIdUser() throws Exception {
            mockMvc.perform(get("/api/accounts/2"))
                .andExpect(jsonPath("$.email").value("user@gmail.com"));
        }

        @Test
        void testViewByIdNotFound() throws Exception{
            mockMvc.perform(get("/api/accounts/99"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Account not found"))
                .andExpect(status().isNotFound());
        }



    }

    @Nested
    class DeleteModuleTest {

        @BeforeEach
        void setUp() {
            doAnswer(invo -> {
                Long id = invo.getArgument(0, Long.class);
                if(id.equals(99L)) throw new NotFoundException(Account.class);
                if(id.equals(1L)) return new AccountUserResponseDto("testemail");

                AccountAdminResponseDto accountDto = new AccountAdminResponseDto();
                accountDto.setEmail("testemail");
                accountDto.setRoles(new ArrayList<>(List.of(new RoleResponseDto(1L, "roleone"), new RoleResponseDto(2L, "roletwo"))));
                accountDto.setId(20L);
                return accountDto;
            }).when(accountService).delete(anyLong());
        }

        @Nested
        class AccountAdminResponseDtoTest {

            @Test
            void shouldReturnResponseWhenAccountIsDeleted() throws Exception {
                mockMvc.perform(delete("/api/accounts/20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(20L))
                    .andExpect(jsonPath("$.email").value("testemail"))
                    .andExpect(jsonPath("$.roles[0].id").value(1L))
                    .andExpect(jsonPath("$.roles[1].id").value(2L))
                    .andExpect(jsonPath("$.roles[0].name").value("roleone"))
                    .andExpect(jsonPath("$.roles[1].name").value("roletwo"));
            }

            @Test
            void shouldReturnNotFoundMessageWhenPathIdIsNotFound() throws Exception {
                mockMvc.perform(delete("/api/accounts/99"))
                    .andExpect(jsonPath("$.message").value("Account not found"));
            }

        }


        @Nested
        class AccountUserResponseDtoTest {

            @Test
            void shouldReturnResponseWhenAccountIsDeleted() throws Exception {
                mockMvc.perform(delete("/api/accounts/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("testemail"));
            }

            @Test
            void shouldReturnNotFoundMessageWhenPathIdIsNotFound() throws Exception {
                mockMvc.perform(delete("/api/accounts/99"))
                    .andExpect(jsonPath("$.message").value("Account not found"));
            }

        }



    }

    @Nested
    class UpdateModuleTest {

        AccountUpdateRequestDto requestDto;

        @BeforeEach
        void setUp() {
            requestDto = new AccountUpdateRequestDto();
            requestDto.setEmail("email@gmail.com");
            requestDto.setEnabled(true);
            requestDto.setPassword("12345");
            requestDto.setPersonId(1L);
            requestDto.setRoles(new ArrayList<>(List.of(new RoleRequestDto(1L, "role"))));
            
            doAnswer(invo -> {
                AccountAdminResponseDto responseDto = null;
                if(invo.getArgument(0, Long.class).equals(1L)) {

                    AccountUpdateRequestDto argRequest = invo.getArgument(1, AccountUpdateRequestDto.class);

                    Account tempAcc = new Account();
                    tempAcc.setEmail(argRequest.getEmail());
                    tempAcc.getAudit().setEnabled(argRequest.isEnabled());
                    tempAcc.setPassword(argRequest.getPassword());
                    tempAcc.setPerson(getPersonInstance());
                    tempAcc.getPerson().setId(argRequest.getPersonId());
                    tempAcc.setRoles(new ArrayList<>((argRequest.getRoles().stream().map(RoleMapper::requestToEntity).collect(Collectors.toList()))));

                    responseDto = (AccountAdminResponseDto) AccountMapper.entityToAdminResponse(tempAcc);
                    responseDto.setId(1L);
                } else {
                    throw new NotFoundException(Account.class);
                }
                return responseDto;
            }).when(accountService).update(anyLong(), any(AccountUpdateRequestDto.class));
        }


        @Test
        void shouldReturnResponseWhenAccountIsUpdated() throws Exception {
            mockMvc.perform(patch("/api/accounts/1")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("email@gmail.com"))
                .andExpect(jsonPath("$.audit.enabled").value(true))
                .andExpect(jsonPath("$.personId").value(1L))
                .andExpect(jsonPath("$.roles[0].id").value(1L))
                .andExpect(jsonPath("$.roles[0].name").value("role"))
                .andExpect(status().isOk());
        }

        @Test
        void shouldReturnNotFoundMessageWhenPathIdIsNotFound() throws Exception {
            mockMvc.perform(patch("/api/accounts/2")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account not found"));
        }

        @Test
        void shouldReturnFieldMessageWhenFieldIsInvalid() throws Exception {
            requestDto.setEmail("email");
            mockMvc.perform(patch("/api/accounts/1")
                    .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("the field email must be a well-formed email address"));
        }


    }

    
}


