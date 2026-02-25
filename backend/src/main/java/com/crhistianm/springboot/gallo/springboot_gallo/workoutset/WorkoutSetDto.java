package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.group.FirstCheck;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.group.SecondCheck;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.group.ThirdCheck;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

class WorkoutSetDto extends AbstractWorkoutSetDto {

    WorkoutSetDto(){
        super(null, null, false);
    }

    WorkoutSetDto(Integer repAmount, Double weigthAmount, boolean toFailure) {
        super(repAmount, weigthAmount, toFailure);
    }

    @Override
    @NotNull(groups = FirstCheck.class)
    @Max(value = 100, groups = SecondCheck.class)
    @Min(value = 1, groups = SecondCheck.class)
    Integer getRepAmount() {
        return super.getRepAmount();
    }

    @Override
    @NotNull(groups = FirstCheck.class)
    @Digits(integer = 3, fraction = 2, groups = SecondCheck.class)
    @DecimalMax(value = "600.00", groups = ThirdCheck.class)
    @DecimalMin(value = "020.00", groups = ThirdCheck.class)
    Double getWeightAmount() {
        return super.getWeightAmount();
    }

    @Override
    boolean isToFailure() {
        return super.isToFailure();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getRepAmount() == null) ? 0 : this.getRepAmount().hashCode());
        result = prime * result + ((this.getWeightAmount() == null) ? 0 : this.getWeightAmount().hashCode());
        result = prime * result + (this.isToFailure() ? 1231 : 1237);
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
        WorkoutSetDto other = (WorkoutSetDto) obj;
        if (this.getRepAmount() == null) {
            if (other.getRepAmount() != null)
                return false;
        } else if (!this.getRepAmount().equals(other.getRepAmount()))
            return false;
        if (this.getWeightAmount() == null) {
            if (other.getWeightAmount()!= null)
                return false;
        } else if (!this.getWeightAmount().equals(other.getWeightAmount()))
            return false;
        if (this.isToFailure() != other.isToFailure())
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WorkoutSetDto [repAmount=" + this.getRepAmount() + ", weightAmount=" + this.getWeightAmount() + ", toFailure="
                + this.isToFailure() + "]";
    }

}
