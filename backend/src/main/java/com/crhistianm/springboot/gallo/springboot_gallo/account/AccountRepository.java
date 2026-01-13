package com.crhistianm.springboot.gallo.springboot_gallo.account;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

interface AccountRepository extends CrudRepository <Account, Long>{

    Optional<Account> findByEmail(String email);

    @Query("select a from Account a left join fetch a.roles where a.email=?1")
    Optional<Account> findByEmailWithRoles(String email);

    boolean existsByEmail(String email);

    @Query("select a from Account a where a.person.id=?1")
    Optional<Account> findAccountByPersonId(Long personId);

    List<Account> findAll();

}
