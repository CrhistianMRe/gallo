
package com.crhistianm.springboot.gallo.springboot_gallo.bodypart;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

interface BodyPartRepository extends CrudRepository<BodyPart, Long>{

    List<BodyPart> findAll();
    
}
