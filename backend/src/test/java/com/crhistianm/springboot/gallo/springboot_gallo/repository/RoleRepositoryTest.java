package com.crhistianm.springboot.gallo.springboot_gallo.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql("/roleinserts.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testRole(){
        assertTrue(roleRepository.findByName("ROLE_USER").isPresent());
        assertTrue(roleRepository.findByName("ROLE_ADMIN").isPresent());
    }

    
}
