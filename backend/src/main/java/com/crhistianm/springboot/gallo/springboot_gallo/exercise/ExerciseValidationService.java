package com.crhistianm.springboot.gallo.springboot_gallo.exercise;

import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.shared.FieldInfoErrorBuilder;

@Service
public class ExerciseValidationService {

    private final ExerciseRepository exerciseRepository;

    private final Environment env;

    ExerciseValidationService(ExerciseRepository exerciseRepository, Environment env) {
        this.exerciseRepository = exerciseRepository;
        this.env = env;
    }

    public Optional<FieldInfoError> validateExerciseExistence(Long exerciseId) {
        FieldInfoError field = null;
        if(!exerciseExists(exerciseId)) {
            field = new FieldInfoErrorBuilder()
                .name("exerciseId")
                .errorMessage(env.getProperty("exercise.validation.ExerciseExistence"))
                .type(exerciseId.getClass())
                .value(exerciseId)
                .build();
        }

        return Optional.ofNullable(field);
    }

    @Transactional(readOnly = true)
    boolean exerciseExists(Long exerciseId) {
        return exerciseRepository.existsById(exerciseId);
    }
    
}
