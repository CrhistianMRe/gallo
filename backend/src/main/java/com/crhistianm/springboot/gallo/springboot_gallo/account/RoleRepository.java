package com.crhistianm.springboot.gallo.springboot_gallo.account;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

interface RoleRepository extends CrudRepository <Role, Long>{

    Optional<Role> findByName(String name);

    boolean existsByName(String name);
    
}
