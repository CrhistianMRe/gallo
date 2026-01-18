package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import java.time.LocalDate;
import java.util.List;

import com.crhistianm.springboot.gallo.springboot_gallo.account.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.exercise.Exercise;

class WorkoutBuilder {

    private Long id;

    private LocalDate workoutDate;

    private short workoutLength;

    private Exercise exercise;

    private Account account;

    private List<WorkoutSet> sets;

    WorkoutBuilder() {
    }

    WorkoutBuilder id(Long id){
        this.id = id;
        return this;
    }

    WorkoutBuilder workoutDate(LocalDate workoutDate){
        this.workoutDate = workoutDate;
        return this;
    }

    WorkoutBuilder workoutLength(short workoutLength){
        this.workoutLength = workoutLength;
        return this;
    }

    WorkoutBuilder exercise(Exercise exercise){
        this.exercise = exercise;
        return this;
    }

    WorkoutBuilder account(Account account){
        this.account = account;
        return this;
    }

    WorkoutBuilder sets(List<WorkoutSet> sets){
        this.sets = sets;
        return this;
    }

    Workout build(){
        Workout workout = new Workout();
        workout.setId(this.id);
        workout.setWorkoutDate(this.workoutDate);
        workout.setWorkoutLength(this.workoutLength);
        workout.setExercise(this.exercise);
        workout.setAccount(this.account);
        workout.setSets(this.sets);
        return workout;
    }

    
}

