package com.crhistianm.springboot.gallo.springboot_gallo.exercise;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

interface ExerciseRepository extends CrudRepository<Exercise, Long> {

    List<Exercise> findAll();

}
