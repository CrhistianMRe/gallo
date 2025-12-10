package com.crhistianm.springboot.gallo.springboot_gallo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.Workout;

public interface WorkoutRepository extends CrudRepository<Workout, Long>{

    Page<Workout> findByAccountId(Long accountId, Pageable pageable);

}
    

