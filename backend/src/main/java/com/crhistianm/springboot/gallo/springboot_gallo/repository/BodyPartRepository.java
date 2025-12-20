
package com.crhistianm.springboot.gallo.springboot_gallo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.BodyPart;

public interface BodyPartRepository extends CrudRepository<BodyPart, Long>{

    public List<BodyPart> findAll();
    
}
