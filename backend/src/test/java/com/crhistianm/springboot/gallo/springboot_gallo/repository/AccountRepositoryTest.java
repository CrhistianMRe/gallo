package com.crhistianm.springboot.gallo.springboot_gallo.repository;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.createAdminRole;
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.createUserRole;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.AccountBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.builder.PersonBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @Sql("/personinserts.sql")
    void testSave(){
        Account account = new AccountBuilder().email("example@gmail.com").person(new PersonBuilder().firstName("two").id(1L).build()).build();
        accountRepository.save(account);

        assertTrue(accountRepository.findByEmail("example@gmail.com").isPresent());
        assertEquals("example@gmail.com", accountRepository.findByEmail("example@gmail.com").orElseThrow().getEmail());
    }

    @Test
    @Sql({"/personinserts.sql", "/accountinserts.sql"})
    void testFindAccountByPersonId(){
        Account account = new AccountBuilder()
            .id(1L)
            .email("crhistian@gmail.com")
            .build();


        Optional<Account> accountOptional = accountRepository.findAccountByPersonId(1L);

        assertTrue(accountOptional.isPresent());
        assertEquals(account, accountOptional.orElseThrow());
    }

    @Test
    @Sql({"/personinserts.sql", "/roleinserts.sql", "/accountinserts.sql", "/accountroleinserts.sql"})
    void testFindByEmailWithRoles(){
        Account account = new AccountBuilder()
            .id(1L)
            .email("crhistian@gmail.com")
            .person(new PersonBuilder()
                    .firstName("Crhistian").lastName("Mendez").phoneNumber("111222333").birthDate(LocalDate.of(2000, 01, 01)).weight(80.0).gender("M").height(1.74).build())
            .roles(List.of(createAdminRole().orElseThrow(), createUserRole().orElseThrow()))
            .build();

        Optional<Account> accountOptional = accountRepository.findByEmailWithRoles("crhistian@gmail.com");

        assertTrue(accountOptional.isPresent());
        assertThat(accountOptional.orElseThrow().getRoles()).hasSize(2);

        assertEquals(account.getEmail(), accountOptional.orElseThrow().getEmail());
        assertEquals(account, accountOptional.orElseThrow());
    }

}
