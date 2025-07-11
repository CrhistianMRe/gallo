package com.crhistianm.springboot.gallo.springboot_gallo.builder;

import com.crhistianm.springboot.gallo.springboot_gallo.entity.Workout;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.WorkoutSet;

public class WorkoutSetBuilder {

    private Long id;

    private Integer repAmount;

    private Double weightAmount;

    private Boolean toFailure;

    private Workout workout;

    public WorkoutSetBuilder(){}

    public WorkoutSetBuilder id(Long id){
        this.id = id;
        return this;
    }

    public WorkoutSetBuilder repAmount(Integer repAmount){
        this.repAmount = repAmount;
        return this;
    }

    public WorkoutSetBuilder weightAmount(Double weightAmount){
        this.weightAmount = weightAmount;
        return this;
    }

    public WorkoutSetBuilder toFailure(Boolean toFailure){
        this.toFailure = toFailure;
        return this;
    }

    public WorkoutSetBuilder workout(Workout workout){
        this.workout = workout;
        return this;
    }

    public WorkoutSet build(){
        WorkoutSet workoutSet = new WorkoutSet();
        workoutSet.setId(this.id);
        workoutSet.setRepAmount(this.repAmount);
        workoutSet.setWeightAmount(this.weightAmount);
        workoutSet.setToFailure(this.toFailure);
        workoutSet.setWorkout(this.workout);
        return workoutSet;
    }

}

