package com.crhistianm.springboot.gallo.springboot_gallo.workout;

class WorkoutMapper {

    static WorkoutResponseDto entityToResponse(Workout workout) {
        WorkoutResponseDto responseDto = new WorkoutResponseDto();
        responseDto.setId(workout.getId());
        responseDto.setWorkoutDate(workout.getWorkoutDate());
        responseDto.setWorkoutLength(workout.getWorkoutLength());
        responseDto.setExerciseName(workout.getExercise().getName());
        responseDto.setImageUrl(workout.getExercise().getImageUrl());
        return responseDto;
    }
    
}

