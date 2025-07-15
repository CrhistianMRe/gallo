package com.crhistianm.springboot.gallo.springboot_gallo.entity;


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

@DataJpaTest
@Sql(scripts = "classpath:personinserts.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class AccountTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("Testing per persists createdAt Date")
    void testLifeCyclePersist(){
        Account account = new AccountBuilder().email("example@gmail.com").password("12345").person(new PersonBuilder().firstName("example").build()).build();
        boolean result = false;
        Account accountResult = accountRepository.save(account);
        if(accountResult.getAudit().getCreatedAt() != null){
            result = true;
            System.out.println("Date ________________-----: " + accountResult.getAudit().getCreatedAt());
        };
        assertTrue(result);
    }
    
}
