package com.crhistianm.springboot.gallo.springboot_gallo.bodypart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class BodyPartServiceUnitTest {

    @Mock
    BodyPartRepository bodyPartRepository;

    @InjectMocks
    BodyPartService bodyPartService;

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
