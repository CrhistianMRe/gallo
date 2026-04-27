
package com.crhistianm.springboot.gallo.springboot_gallo.bodypart;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

interface BodyPartRepository extends CrudRepository<BodyPart, Long>{

    List<BodyPart> findAll();

    @Query(value = "SELECT b FROM BodyPart b INNER JOIN b.exercises e WHERE e.id=?1")
    List<BodyPart> findAllByExerciseId(Long exerciseId);
    
}
