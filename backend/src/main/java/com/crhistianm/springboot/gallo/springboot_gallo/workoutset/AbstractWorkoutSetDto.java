package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

abstract class AbstractWorkoutSetDto {

    private Integer repAmount;

    private Double weightAmount;

    private boolean toFailure;

    AbstractWorkoutSetDto() {}

    AbstractWorkoutSetDto(Integer repAmount, Double weightAmount, boolean toFailure) {
        this.repAmount = repAmount;
        this.weightAmount = weightAmount;
        this.toFailure = toFailure;
    }

    Integer getRepAmount() {
        return repAmount;
    }

    void setRepAmount(Integer repAmount) {
        this.repAmount = repAmount;
    }

    Double getWeightAmount() {
        return weightAmount;
    }

    void setWeightAmount(Double weightAmount) {
        this.weightAmount = weightAmount;
    }

    boolean isToFailure() {
        return toFailure;
    }

    void setToFailure(boolean toFailure) {
        this.toFailure = toFailure;
    }

    @Override
    abstract public int hashCode();

    @Override
    abstract public boolean equals(Object obj);

    @Override
    abstract public String toString();

}
