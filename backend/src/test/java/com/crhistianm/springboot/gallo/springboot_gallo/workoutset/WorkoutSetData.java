package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import java.util.ArrayList;
import java.util.List;

public class WorkoutSetData {

    public static WorkoutSet getWorkoutSetInstance(){ return new WorkoutSet(); }

    static List<SetRequestDto> givenSetRequestDtoList() {

        Integer repAmount = 20;

        Double weightAmount = 20.00;

        boolean toFailure = true;

        SetRequestDto firstSet = new SetRequestDto(repAmount, weightAmount, toFailure);

        repAmount = 40;

        weightAmount = 40.00;

        toFailure = true;

        SetRequestDto secondSet = new SetRequestDto(repAmount, weightAmount, toFailure);

        repAmount = 60;

        weightAmount = 60.00;

        toFailure = true;

        SetRequestDto thirdSet = new SetRequestDto(repAmount, weightAmount, toFailure);

        repAmount = 80;

        weightAmount = 80.00;

        toFailure = false;

        SetRequestDto fourthSet = new SetRequestDto(repAmount, weightAmount, toFailure);

        List<SetRequestDto> setDtoList = new ArrayList<>();

        setDtoList.add(firstSet);
        setDtoList.add(secondSet);
        setDtoList.add(thirdSet);
        setDtoList.add(fourthSet);
        
        return setDtoList;
    }
}
