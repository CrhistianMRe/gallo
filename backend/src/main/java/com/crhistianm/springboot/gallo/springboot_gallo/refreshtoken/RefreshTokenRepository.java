package com.crhistianm.springboot.gallo.springboot_gallo.refreshtoken;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    
}
