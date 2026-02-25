package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import org.springframework.data.repository.CrudRepository;

interface WorkoutSetRepository extends CrudRepository<WorkoutSet, Long> {

    short countByWorkoutId(Long workoutId);

}
