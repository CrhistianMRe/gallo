package com.crhistianm.springboot.gallo.springboot_gallo.bodypart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.shared.exception.ValidationServiceException;

@ExtendWith(MockitoExtension.class)
class BodyPartServiceUnitTest {

    @Mock
    private BodyPartRepository bodyPartRepository;

    @Mock
    private BodyPartValidator bodyPartValidator;

    @InjectMocks
    private BodyPartService bodyPartService;

    @Nested
    class GetAllMethodTest {

        @BeforeEach
        void setUp() {
            List<BodyPart> entityBodyParts = new ArrayList<>();
            entityBodyParts.add(new BodyPartBuilder().name("part1").id(1L).build());
            entityBodyParts.add(new BodyPartBuilder().name("part2").id(2L).build());
            entityBodyParts.add(new BodyPartBuilder().name("part3").id(3L).build());
            doReturn(entityBodyParts).when(bodyPartRepository).findAll();
        }

        @Test
        void shouldReturnDtoListWhenMethodIsCalled() {
            List<BodyPartResponseDto> bodyPartsReponse = bodyPartService.getAll();

            assertThat(bodyPartsReponse).isNotEmpty();
            assertThat(bodyPartsReponse).hasSize(3);

            assertThat(bodyPartsReponse).extracting(BodyPartResponseDto::getName).contains("part1");
            assertThat(bodyPartsReponse).extracting(BodyPartResponseDto::getName).contains("part2");
            assertThat(bodyPartsReponse).extracting(BodyPartResponseDto::getName).contains("part3");

            assertThat(bodyPartsReponse).extracting(BodyPartResponseDto::getId).contains(1L);
            assertThat(bodyPartsReponse).extracting(BodyPartResponseDto::getId).contains(2L);
            assertThat(bodyPartsReponse).extracting(BodyPartResponseDto::getId).contains(3L);

            verify(bodyPartRepository, times(1)).findAll();
        }

    }

    @Nested
    class GetAllByExerciseIdMethodTest {

        private Long exerciseId;

        @BeforeEach
        void setUp() {
            doAnswer(invo -> {
                final Long argExerciseID = invo.getArgument(0, Long.class);
                if(argExerciseID.equals(99L)) throw new ValidationServiceException();
                return null;
            }).when(bodyPartValidator).validateByIdRequest(anyLong());

            lenient().doAnswer(invo -> {
                List<BodyPart> responseEntityList = new ArrayList<>();

                responseEntityList.add(new BodyPartBuilder().name("part1").build());
                responseEntityList.add(new BodyPartBuilder().name("part2").build());
                return responseEntityList;

            }).when(bodyPartRepository).findAllByExerciseId(anyLong());

        }

        @Test
        void shouldReturnResponseList() {
            exerciseId = 1L;

            List<BodyPartResponseDto> expectedResponseList = bodyPartService.getAllByExerciseId(exerciseId);

            assertThat(expectedResponseList).isNotEmpty();

            verify(bodyPartRepository, times(1)).findAllByExerciseId(eq(exerciseId));
            verify(bodyPartValidator, times(1)).validateByIdRequest(eq(exerciseId));
        }

        @Test
        void shouldThrowExceptionWhenRequestIsInvalid() {   
            exerciseId = 99L;

            assertThatException().isThrownBy(() -> bodyPartService.getAllByExerciseId(exerciseId));

            verify(bodyPartValidator, times(1)).validateByIdRequest(eq(exerciseId));
            verifyNoInteractions(bodyPartRepository);
        }

    }

}
