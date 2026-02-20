package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import com.crhistianm.springboot.gallo.springboot_gallo.workout.Workout;

class WorkoutSetBuilder {

    private Long id;

    private Byte repAmount;

    private Double weightAmount;

    private Boolean toFailure;

    private Workout workout;

    WorkoutSetBuilder(){}

    WorkoutSetBuilder id(Long id){
        this.id = id;
        return this;
    }

    WorkoutSetBuilder repAmount(Byte repAmount){
        this.repAmount = repAmount;
        return this;
    }

    WorkoutSetBuilder weightAmount(Double weightAmount){
        this.weightAmount = weightAmount;
        return this;
    }

    WorkoutSetBuilder toFailure(Boolean toFailure){
        this.toFailure = toFailure;
        return this;
    }

    WorkoutSetBuilder workout(Workout workout){
        this.workout = workout;
        return this;
    }

    WorkoutSet build(){
        WorkoutSet workoutSet = new WorkoutSet();
        workoutSet.setId(this.id);
        workoutSet.setRepAmount(this.repAmount);
        workoutSet.setWeightAmount(this.weightAmount);
        workoutSet.setToFailure(this.toFailure);
        workoutSet.setWorkout(this.workout);
        return workoutSet;
    }

}

