package com.crhistianm.springboot.gallo.springboot_gallo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.crhistianm.springboot.gallo.springboot_gallo.builder.BodyPartBuilder;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.BodyPartResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.BodyPart;

public class BodyPartMapperUnitTest {

    @Test
    void shouldReturnReponseDtoFromEntity() {
        BodyPart bodyPartEntity = new BodyPartBuilder().name("part1").id(1L).build();
        BodyPartResponseDto responseDto = assertThat(BodyPartMapper.entityToResponse(bodyPartEntity)).isInstanceOf(BodyPartResponseDto.class).actual();
        assertThat(responseDto).extracting(BodyPartResponseDto::getName).isEqualTo("part1");
        assertThat(responseDto).extracting(BodyPartResponseDto::getId).isEqualTo(1L);
    }

    
}
