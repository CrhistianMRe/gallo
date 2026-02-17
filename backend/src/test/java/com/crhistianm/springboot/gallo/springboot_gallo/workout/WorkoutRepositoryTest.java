package com.crhistianm.springboot.gallo.springboot_gallo.workout;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class WorkoutRepositoryTest {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Nested
    @Sql({"/personinserts.sql", "/accountinserts.sql", "/exerciseinserts.sql", "/workoutinserts.sql"})
    class CountPerDayByAccountAndExerciseMethodTest {

        int count;

        @BeforeEach
        void setUp() {
            count = 99999;
        }

        @Test
        void shouldReturnCountOfZeroWhenNoWorkoutsMatch() {
            count = workoutRepository.countPerDayByAccountAndExercise(1L, LocalDate.of(2004, 9, 21), 10L);
            assertThat(count).isEqualTo(0);
        }

        @Test
        void shouldReturnCorrectCountWhenFiltersMatch() {
            count = workoutRepository.countPerDayByAccountAndExercise(1L, LocalDate.of(2004, 9, 21), 1L);
            assertThat(count).isEqualTo(3);
        }

        @Test
        void shouldReturnMatchingDifferentAccountWorkouts() {
            count = workoutRepository.countPerDayByAccountAndExercise(2L, LocalDate.of(2004, 9, 21), 1L);
            assertThat(count).isEqualTo(5);
        }

        @Test
        void shouldReturnMatchingDifferentExerciseWorkouts() {
            count = workoutRepository.countPerDayByAccountAndExercise(1L, LocalDate.of(2004, 9, 21), 4L);
            assertThat(count).isEqualTo(2);
        }

        @Test
        void shouldReturnMatchingDifferentDateWorkouts() {
            count = workoutRepository.countPerDayByAccountAndExercise(2L, LocalDate.of(2010, 1, 1), 4L);
            assertThat(count).isEqualTo(4);
        }

    }
    
}
