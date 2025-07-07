package com.crhistianm.springboot.gallo.springboot_gallo.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;

public interface AccountRepository extends CrudRepository <Account, Long>{

    Optional<Account> findByEmail(String email);

}
