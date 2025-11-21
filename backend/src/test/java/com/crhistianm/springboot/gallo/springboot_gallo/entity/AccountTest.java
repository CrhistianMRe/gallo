package com.crhistianm.springboot.gallo.springboot_gallo.entity;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.AccountBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.builder.PersonBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.AccountRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AccountTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Testing per persists createdAt Date")
    @Sql(scripts = "classpath:personinserts.sql")
    void testLifeCyclePersist(){
        Account account = new AccountBuilder().email("example@gmail.com").password("12345").person(new PersonBuilder().firstName("example").build()).build();
        boolean result = false;
        Account accountResult = accountRepository.save(account);
        if(accountResult.getAudit().getCreatedAt() != null){
            result = true;
        };
        assertTrue(result);
    }

    @Test
    @Sql(scripts = {"classpath:personinserts.sql", "classpath:accountinserts.sql"})
    void shouldSaveUpdateTransactionTimeWhenUpdateRequestIsReceived() { 
        Account account = accountRepository.findById(1L).orElseThrow();
        account.setEmail("update@gmail.com");

        Account accountResult = accountRepository.save(account);
        entityManager.flush();

        assertThat(accountResult).extracting(Account::getEmail).isEqualTo("update@gmail.com");
        assertThat(accountResult.getAudit()).isNotNull();
        assertThat(accountResult.getAudit().getUpdatedAt()).isNotNull();
    }

    
}
