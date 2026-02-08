package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import java.time.LocalDate;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.RequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.group.FirstCheck;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.group.SecondCheck;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

class WorkoutRequestDto implements RequestDto {

    @NotNull(groups = FirstCheck.class)
    @PresentDay(groups = SecondCheck.class)
    private LocalDate workoutDate;

    @Digits(integer = 5, fraction = 0, groups = FirstCheck.class)
    @Min(value = 20, groups = SecondCheck.class)
    @Max(value = Short.MAX_VALUE-1, groups = SecondCheck.class)
    private short workoutLength;

    @NotNull(groups = FirstCheck.class)
    private Long accountId;

    @NotNull(groups = FirstCheck.class)
    private Long exerciseId;

    WorkoutRequestDto() {}

    WorkoutRequestDto(Long accountId, LocalDate workoutDate, short workoutLength, Long exerciseId) {
        this.accountId = accountId;
        this.workoutDate = workoutDate;
        this.workoutLength = workoutLength;
        this.exerciseId = exerciseId;
    }

    Long getAccountId() {
        return accountId;
    }

    void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    LocalDate getWorkoutDate() {
        return workoutDate;
    }

    void setWorkoutDate(LocalDate workoutDate) {
        this.workoutDate = workoutDate;
    }

    short getWorkoutLength() {
        return workoutLength;
    }

    void setWorkoutLength(short workoutLength) {
        this.workoutLength = workoutLength;
    }

    Long getExerciseId() {
        return exerciseId;
    }

    void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
        result = prime * result + ((workoutDate == null) ? 0 : workoutDate.hashCode());
        result = prime * result + workoutLength;
        result = prime * result + ((exerciseId == null) ? 0 : exerciseId.hashCode());
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
        WorkoutRequestDto other = (WorkoutRequestDto) obj;
        if (accountId == null) {
            if (other.accountId != null)
                return false;
        } else if (!accountId.equals(other.accountId))
            return false;
        if (workoutDate == null) {
            if (other.workoutDate != null)
                return false;
        } else if (!workoutDate.equals(other.workoutDate))
            return false;
        if (workoutLength != other.workoutLength)
            return false;
        if (exerciseId == null) {
            if (other.exerciseId != null)
                return false;
        } else if (!exerciseId.equals(other.exerciseId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WorkoutRequestDto [workoutDate=" + workoutDate + ", workoutLength=" + workoutLength + ", accountId="
                + accountId + ", exerciseId=" + exerciseId + "]";
    }

}
