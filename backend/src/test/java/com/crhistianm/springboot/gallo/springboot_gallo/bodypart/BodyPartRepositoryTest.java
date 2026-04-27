package com.crhistianm.springboot.gallo.springboot_gallo.bodypart;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BodyPartRepositoryTest {

    @Autowired
    private BodyPartRepository bodyPartRepository;

    @Nested
    @Sql(value = {"/exerciseinserts.sql", "/bodypartinserts.sql", "/exercisebodypartinserts.sql"})
    class FindAllByExerciseIdMethodTest {

        Long exerciseId;

        @Test
        void shouldReturnEmptyListWhenExerciseDoesNotExist() {
            exerciseId = 3L;

            List<BodyPart> entityList = bodyPartRepository.findAllByExerciseId(exerciseId);

            assertThat(entityList).isEmpty();
        }

        @Test
        void shouldReturnListWhenExerciseBodyPartsIsFound() {
            exerciseId = 1L;

            List<BodyPart> entityList = bodyPartRepository.findAllByExerciseId(exerciseId);

            assertThat(entityList).isNotEmpty();
            assertThat(entityList).hasSize(2);

            assertThat(entityList).extracting(BodyPart::getName).containsOnlyOnce("part1");
            assertThat(entityList).extracting(BodyPart::getName).containsOnlyOnce("part2");

            assertThat(entityList).extracting(BodyPart::getId).containsOnlyOnce(1L);
            assertThat(entityList).extracting(BodyPart::getId).containsOnlyOnce(2L);
        }

    }
    
}
