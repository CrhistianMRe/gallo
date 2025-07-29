package com.crhistianm.springboot.gallo.springboot_gallo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;

public interface AccountRepository extends CrudRepository <Account, Long>{

    Optional<Account> findByEmail(String email);

    @Query("select a from Account a left join fetch a.roles where a.email=?1")
    Optional<Account> findByEmailWithRoles(String email);

    boolean existsByEmail(String email);

    @Query("select a from Account a where a.person.id=?1")
    Optional<Account> findAccountByPersonId(Long personId);

}
