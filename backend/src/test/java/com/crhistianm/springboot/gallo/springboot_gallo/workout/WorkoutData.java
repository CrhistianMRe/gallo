package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import static com.crhistianm.springboot.gallo.springboot_gallo.account.AccountData.getAccountInstance;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.crhistianm.springboot.gallo.springboot_gallo.account.Account;
import com.crhistianm.springboot.gallo.springboot_gallo.exercise.Exercise;
import static com.crhistianm.springboot.gallo.springboot_gallo.exercise.ExerciseData.getExerciseInstance;
class WorkoutData {


    static Optional<Exercise> givenLegExercise() {
        Exercise exercise = getExerciseInstance();
        exercise.setId(1L);
        exercise.setName("Leg press");
        exercise.setDescription("Leg press");
        exercise.setImageUrl("imageUrl.com");
        exercise.setWeightRequired(true);
        return Optional.of(exercise);
    }

    static Optional<WorkoutSet> givenWorkoutSet() {
        return Optional.of(new WorkoutSetBuilder()
            .weightAmount(80.0)
            .toFailure(false)
            .build());
    }


    static Optional<Account> givenAccountEntityAdmin(){
        Account account = getAccountInstance();
        account.setId(1L);
        account.setEmail("admin@gmail.com");
        account.setPassword("12345");
        account.setWorkouts(null);
        return Optional.ofNullable(account);
    }

    static Optional<Workout> givenWorkout() {
        return Optional.of(new WorkoutBuilder()
            .workoutLength(120.0)
            .workoutDate(LocalDate.of(2000, 01, 01))
            .account(givenAccountEntityAdmin().orElseThrow())
            .exercise(givenLegExercise().orElseThrow())
            .build());
    }

    static List<Workout> givenWorkoutList() {

        /*4 sets per workout*/
        WorkoutSet firstSet = givenWorkoutSet().orElseThrow();
        firstSet.setId(1L);
        firstSet.setRepAmount(12);

        WorkoutSet secondSet = givenWorkoutSet().orElseThrow();
        secondSet.setId(2L);
        secondSet.setRepAmount(10);

        WorkoutSet thirdSet = givenWorkoutSet().orElseThrow();
        thirdSet.setId(3L);
        thirdSet.setRepAmount(8);

        WorkoutSet fourthSet = givenWorkoutSet().orElseThrow();
        fourthSet.setId(4L);
        fourthSet.setToFailure(true);
        fourthSet.setRepAmount(6);

        List<WorkoutSet> setList = new ArrayList<>(List.of(firstSet, secondSet, thirdSet, fourthSet));

        Workout firstWorkout = givenWorkout().orElseThrow();
        firstWorkout.setId(1L);

        Workout secondWorkout = givenWorkout().orElseThrow();
        secondWorkout.setId(2L);

        Workout thirdWorkout = givenWorkout().orElseThrow();
        thirdWorkout.setId(3L);
       
        Workout fourthWorkout = givenWorkout().orElseThrow();
        fourthWorkout.setId(4L);

        /*assign bidirectional relationship per workout*/
        setList.forEach(i-> i.setWorkout(firstWorkout));
        firstWorkout.setSets(setList);

        setList.forEach(i-> i.setWorkout(secondWorkout));
        secondWorkout.setSets(setList);

        setList.forEach(i-> i.setWorkout(thirdWorkout));
        thirdWorkout.setSets(setList);

        setList.forEach(i-> i.setWorkout(fourthWorkout));
        fourthWorkout.setSets(setList);

        return new ArrayList<>(List.of(firstWorkout, secondWorkout, thirdWorkout, fourthWorkout));
    }
}
