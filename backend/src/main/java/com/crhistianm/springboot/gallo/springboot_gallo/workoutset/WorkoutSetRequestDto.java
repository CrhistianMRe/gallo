package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import java.util.ArrayList;
import java.util.List;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.RequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.group.FirstCheck;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

class WorkoutSetRequestDto implements RequestDto {

    @NotNull(groups = FirstCheck.class)
    private Long workoutId;

    @NotEmpty(groups = FirstCheck.class)
    List<@Valid WorkoutSetDto> sets;

    WorkoutSetRequestDto() {
        this.sets = new ArrayList<>();
    }

    WorkoutSetRequestDto(Long workoutId) {
        this();
        this.workoutId = workoutId;
    }

    Long getWorkoutId() {
        return workoutId;
    }

    void setWorkoutId(Long workoutId) {
        this.workoutId = workoutId;
    }

    List<WorkoutSetDto> getSets() {
        return sets;
    }

    void setSets(List<WorkoutSetDto> sets) {
        this.sets = sets;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((workoutId == null) ? 0 : workoutId.hashCode());
        result = prime * result + ((sets == null) ? 0 : sets.hashCode());
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
        WorkoutSetRequestDto other = (WorkoutSetRequestDto) obj;
        if (workoutId == null) {
            if (other.workoutId != null)
                return false;
        } else if (!workoutId.equals(other.workoutId))
            return false;
        if (sets == null) {
            if (other.sets != null)
                return false;
        } else if (!sets.equals(other.sets))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WorkoutSetRequestDto [workoutId=" + workoutId + ", sets=" + sets + "]";
    }

}
