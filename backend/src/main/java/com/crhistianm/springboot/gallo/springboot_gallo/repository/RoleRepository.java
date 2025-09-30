package com.crhistianm.springboot.gallo.springboot_gallo.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.Role;

public interface RoleRepository extends CrudRepository <Role, Long>{

    Optional<Role> findByName(String name);

    boolean existsByName(String name);
    
}
