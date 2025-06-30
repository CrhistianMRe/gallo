package com.crhistianm.springboot.gallo.springboot_gallo.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "workout")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date workoutDate;

    private Double workoutLength;

    //This relationship is not bidirectional 
    @ManyToOne
    private Exercise exercise;

    //bidirectional
    @ManyToOne
    private Account account;

    @OneToMany(mappedBy = "workout")
    private List<WorkoutSet> sets;

    public Workout() {
        this.sets = new ArrayList<>();
    }

    public Workout(Date workoutDate, Double workoutLength, Exercise exercise, Account account) {
        this();
        this.workoutDate = workoutDate;
        this.workoutLength = workoutLength;
        this.exercise = exercise;
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(Date workoutDate) {
        this.workoutDate = workoutDate;
    }

    public Double getWorkoutLength() {
        return workoutLength;
    }

    public void setWorkoutLength(Double workoutLength) {
        this.workoutLength = workoutLength;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setSets(List<WorkoutSet> sets) {
        this.sets = sets;
    }

    public List<WorkoutSet> getSets() {
        return sets;
    }

    @Override
    public String toString() {
        return "Workout {id=" + id + ", workoutDate=" + workoutDate + ", workoutLength=" + workoutLength + ", exercise="
                + exercise + ", sets=" + sets + "}";
    }



}
