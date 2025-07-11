package com.crhistianm.springboot.gallo.springboot_gallo.builder;

import java.util.Date;
import java.util.List;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Exercise;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Workout;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.WorkoutSet;

public class WorkoutBuilder {

    private Long id;

    private Date workoutDate;

    private Double workoutLength;

    private Exercise exercise;

    private Account account;

    private List<WorkoutSet> sets;

    public WorkoutBuilder() {
    }

    public WorkoutBuilder id(Long id){
        this.id = id;
        return this;
    }

    public WorkoutBuilder workoutDate(Date workoutDate){
        this.workoutDate = workoutDate;
        return this;
    }

    public WorkoutBuilder workoutLength(Double workoutLength){
        this.workoutLength = workoutLength;
        return this;
    }

    public WorkoutBuilder exercise(Exercise exercise){
        this.exercise = exercise;
        return this;
    }

    public WorkoutBuilder account(Account account){
        this.account = account;
        return this;
    }

    public WorkoutBuilder sets(List<WorkoutSet> sets){
        this.sets = sets;
        return this;
    }

    public Workout build(){
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

