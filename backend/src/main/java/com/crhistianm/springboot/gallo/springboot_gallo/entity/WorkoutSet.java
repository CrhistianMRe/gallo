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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((repAmount == null) ? 0 : repAmount.hashCode());
        result = prime * result + ((weightAmount == null) ? 0 : weightAmount.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkoutSet other = (WorkoutSet) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (repAmount == null) {
            if (other.repAmount != null)
                return false;
        } else if (!repAmount.equals(other.repAmount))
            return false;
        if (weightAmount == null) {
            if (other.weightAmount != null)
                return false;
        } else if (!weightAmount.equals(other.weightAmount))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WorkoutSet [id=" + id + ", repAmount=" + repAmount + ", weightAmount=" + weightAmount + ", toFailure="
                + toFailure + "]";
    }

}
