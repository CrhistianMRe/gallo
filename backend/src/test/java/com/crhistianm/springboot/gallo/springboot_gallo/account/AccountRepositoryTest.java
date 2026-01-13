package com.crhistianm.springboot.gallo.springboot_gallo.account;

import static com.crhistianm.springboot.gallo.springboot_gallo.person.PersonData.getPersonInstance;
import static com.crhistianm.springboot.gallo.springboot_gallo.account.AccountData.givenRoleAdmin;
import static com.crhistianm.springboot.gallo.springboot_gallo.account.AccountData.givenRoleUser;

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

import com.crhistianm.springboot.gallo.springboot_gallo.person.Person;


@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @Sql("/personinserts.sql")
    void testSave(){
        Person person = getPersonInstance();
        person.setFirstName("two");
        person.setId(1L);
        Account account = new AccountBuilder().email("example@gmail.com").person(person).build();
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
        Person person = getPersonInstance();
        person.setFirstName("Crhistian");
        person.setFirstName("Mendez");
        person.setPhoneNumber("111222333");
        person.setBirthDate(LocalDate.of(2000, 01, 01));
        person.setWeight(80.0);
        person.setHeight(1.74);
        person.setGender("M");
        Account account = new AccountBuilder()
            .id(1L)
            .email("crhistian@gmail.com")
            .person(person)
            .roles(List.of(givenRoleAdmin().orElseThrow(), givenRoleUser().orElseThrow()))
            .build();

        Optional<Account> accountOptional = accountRepository.findByEmailWithRoles("crhistian@gmail.com");

        assertTrue(accountOptional.isPresent());
        assertThat(accountOptional.orElseThrow().getRoles()).hasSize(2);

        assertEquals(account.getEmail(), accountOptional.orElseThrow().getEmail());
        assertEquals(account, accountOptional.orElseThrow());
    }

}
