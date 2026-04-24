package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

interface WorkoutSetRepository extends CrudRepository<WorkoutSet, Long> {

    short countByWorkoutId(Long workoutId);

    List<WorkoutSet> findAllByWorkoutId(Long workoutId);

}
