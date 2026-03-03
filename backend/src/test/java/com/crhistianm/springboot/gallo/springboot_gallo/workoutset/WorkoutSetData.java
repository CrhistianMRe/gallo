package com.crhistianm.springboot.gallo.springboot_gallo.workoutset;

import java.util.ArrayList;
import java.util.List;

public class WorkoutSetData {

    public static WorkoutSet getWorkoutSetInstance(){ return new WorkoutSet(); }

    static List<WorkoutSetDto> givenWorkoutSetDtoList() {
        WorkoutSetDto firstSet = new WorkoutSetDto();

        firstSet.setRepAmount(20);
        firstSet.setWeightAmount(20.00);
        firstSet.setToFailure(true);

        WorkoutSetDto secondSet = new WorkoutSetDto();

        secondSet.setRepAmount(40);
        secondSet.setWeightAmount(40.00);
        secondSet.setToFailure(true);

        WorkoutSetDto thirdSet = new WorkoutSetDto();

        thirdSet.setRepAmount(60);
        thirdSet.setWeightAmount(60.00);
        thirdSet.setToFailure(true);

        WorkoutSetDto fourthSet = new WorkoutSetDto();

        fourthSet.setRepAmount(80);
        fourthSet.setWeightAmount(80.00);
        fourthSet.setToFailure(false);

        List<WorkoutSetDto> setDtoList = new ArrayList<>();

        setDtoList.add(firstSet);
        setDtoList.add(secondSet);
        setDtoList.add(thirdSet);
        setDtoList.add(fourthSet);
        
        return setDtoList;
    }
}
