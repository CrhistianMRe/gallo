package com.crhistianm.springboot.gallo.springboot_gallo.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.AccountBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.builder.PersonBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;

@DataJpaTest
@Sql("/personinserts.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void testSave(){
        Account account = new AccountBuilder().email("example@gmail.com").person(new PersonBuilder().firstName("two").id(1L).build()).build();
        accountRepository.save(account);

        assertTrue(accountRepository.findByEmail("example@gmail.com").isPresent());
        assertEquals("example@gmail.com", accountRepository.findByEmail("example@gmail.com").orElseThrow().getEmail());
    }
}
