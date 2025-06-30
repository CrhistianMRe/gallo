package com.crhistianm.springboot.gallo.springboot_gallo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "workout_set")
public class WorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer repAmount;

    private Double weightAmount;

    private Boolean toFailure;

    //Bidirectional
    @ManyToOne
    private Workout workout;

    public WorkoutSet(){
    }

    public WorkoutSet(Integer repAmount, Double weightAmount, Boolean toFailure, Workout workout) {
        this.repAmount = repAmount;
        this.weightAmount = weightAmount;
        this.toFailure = toFailure;
        this.workout = workout;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRepAmount() {
        return repAmount;
    }

    public void setRepAmount(Integer repAmount) {
        this.repAmount = repAmount;
    }

    public Double getWeightAmount() {
        return weightAmount;
    }

    public void setWeightAmount(Double weightAmount) {
        this.weightAmount = weightAmount;
    }

    public Boolean getToFailure() {
        return toFailure;
    }

    public void setToFailure(Boolean toFailure) {
        this.toFailure = toFailure;
    }

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    @Override
    public String toString() {
        return "WorkoutSet [id=" + id + ", repAmount=" + repAmount + ", weightAmount=" + weightAmount + ", toFailure="
                + toFailure + "]";
    }

}
