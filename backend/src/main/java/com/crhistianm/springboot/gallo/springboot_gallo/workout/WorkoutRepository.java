package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

interface WorkoutRepository extends CrudRepository<Workout, Long>{

    Page<Workout> findByAccountId(Long accountId, Pageable pageable);

    @Query("select count(w) from Workout w where w.account.id=?1 and workoutDate=?2 and w.exercise.id=?3")
    int countPerDayByAccountAndExercise(Long accountId, LocalDate workoutDate, Long exerciseId);

}
    

