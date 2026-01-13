package com.crhistianm.springboot.gallo.springboot_gallo.bodypart;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BodyPartMapperUnitTest {

    @Test
    void shouldReturnReponseDtoFromEntity() {
        BodyPart bodyPartEntity = new BodyPartBuilder().name("part1").id(1L).build();
        BodyPartResponseDto responseDto = assertThat(BodyPartMapper.entityToResponse(bodyPartEntity)).isInstanceOf(BodyPartResponseDto.class).actual();
        assertThat(responseDto).extracting(BodyPartResponseDto::getName).isEqualTo("part1");
        assertThat(responseDto).extracting(BodyPartResponseDto::getId).isEqualTo(1L);
    }

    
}
