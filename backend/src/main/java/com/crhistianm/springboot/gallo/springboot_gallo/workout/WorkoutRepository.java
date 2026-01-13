package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

interface WorkoutRepository extends CrudRepository<Workout, Long>{

    Page<Workout> findByAccountId(Long accountId, Pageable pageable);

}
    

