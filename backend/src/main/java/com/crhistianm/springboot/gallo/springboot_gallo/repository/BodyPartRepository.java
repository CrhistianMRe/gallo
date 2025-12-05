
package com.crhistianm.springboot.gallo.springboot_gallo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.BodyPart;

@Repository
public interface BodyPartRepository extends CrudRepository<BodyPart, Long>{

    public List<BodyPart> findAll();
    
}
