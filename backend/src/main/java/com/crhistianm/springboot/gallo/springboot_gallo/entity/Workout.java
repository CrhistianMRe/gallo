package com.crhistianm.springboot.gallo.springboot_gallo.entity;

import java.time.LocalDate;
import java.util.ArrayList;
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

    private LocalDate workoutDate;

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

    public Workout(LocalDate workoutDate, Double workoutLength, Exercise exercise, Account account) {
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

    public LocalDate getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(LocalDate workoutDate) {
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((workoutDate == null) ? 0 : workoutDate.hashCode());
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
        Workout other = (Workout) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (workoutDate == null) {
            if (other.workoutDate != null)
                return false;
        } else if (!workoutDate.equals(other.workoutDate))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Workout {id=" + id + ", workoutDate=" + workoutDate + ", workoutLength=" + workoutLength + ", exercise="
                + exercise + ", sets=" + sets + "}";
    }



}
