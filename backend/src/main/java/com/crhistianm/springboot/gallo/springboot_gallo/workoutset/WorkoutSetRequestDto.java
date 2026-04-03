package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import java.util.List;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.RequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.group.FirstCheck;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

final class WorkoutSetRequestDto implements RequestDto {

    @NotNull(groups = FirstCheck.class)
    private final Long workoutId;

    @NotEmpty(groups = FirstCheck.class)
    private final List<@Valid SetRequestDto> sets;

    WorkoutSetRequestDto(Long workoutId, List<SetRequestDto> sets) {
        this.workoutId = workoutId;
        this.sets = sets == null ? List.of(): List.copyOf(sets);
    }

    Long getWorkoutId() {
        return workoutId;
    }

    List<SetRequestDto> getSets() {
        return sets;
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
