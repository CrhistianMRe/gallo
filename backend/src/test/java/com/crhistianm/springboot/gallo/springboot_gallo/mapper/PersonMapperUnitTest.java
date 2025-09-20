package com.crhistianm.springboot.gallo.springboot_gallo.mapper;

import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenAccountEntityAdmin;
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenPersonEntityOne;
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenPersonRequestDtoOne;
import static com.crhistianm.springboot.gallo.springboot_gallo.data.Data.givenRoleResponseDtoAdmin;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.dto.PersonResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.entity.Person;

@ExtendWith(MockitoExtension.class)
public class PersonMapperUnitTest {

    @Nested
    class RequestToEntityTest {

        @Test
        void returnsPersonEntityFromPersonRequestDto() {
            PersonRequestDto originalPerson = givenPersonRequestDtoOne().orElseThrow();
            Person expectedPerson = PersonMapper.requestToEntity(originalPerson);

            assertThat(expectedPerson.getFirstName()).isEqualTo(originalPerson.getFirstName());
            assertThat(expectedPerson.getLastName()).isEqualTo(originalPerson.getLastName());
            assertThat(expectedPerson.getPhoneNumber()).isEqualTo(originalPerson.getPhoneNumber());
            assertThat(expectedPerson.getBirthDate()).isEqualTo(originalPerson.getBirthDate());
            assertThat(expectedPerson.getGender()).isEqualTo(originalPerson.getGender());
            assertThat(expectedPerson.getWeight()).isEqualTo(originalPerson.getWeight());
            assertThat(expectedPerson.getHeight()).isEqualTo(originalPerson.getHeight());

        }

        @Test
        void shouldThrowExceptionWhenPersonRequestDtoIsNull() {
            assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> {
                    PersonMapper.requestToEntity(null);
                });
        }

    }

    @Nested
    class EntityToResponseTest {

        @Test
        void returnsPersonResponseDtoFromPersonEntity() {
            Person originalPerson = givenPersonEntityOne().orElseThrow();
            PersonResponseDto personResponseDto = PersonMapper.entityToResponse(originalPerson);

            assertThat(personResponseDto.getId()).isEqualTo(originalPerson.getId());
            assertThat(personResponseDto.getFirstName()).isEqualTo(originalPerson.getFirstName());
            assertThat(personResponseDto.getLastName()).isEqualTo(originalPerson.getLastName());
            assertThat(personResponseDto.getPhoneNumber()).isEqualTo(originalPerson.getPhoneNumber());
            assertThat(personResponseDto.getBirthDate()).isEqualTo(originalPerson.getBirthDate());
            assertThat(personResponseDto.getGender()).isEqualTo(originalPerson.getGender());
            assertThat(personResponseDto.getHeight()).isEqualTo(originalPerson.getHeight());
            assertThat(personResponseDto.getWeight()).isEqualTo(originalPerson.getWeight());
            assertThat(personResponseDto.getAccount()).isEqualTo(AccountMapper.entityToAdminResponse(givenAccountEntityAdmin().orElseThrow()));
        }

        @Test
        void shouldThrowExceptionWhenPersonEntityIsNull() { 
            assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> PersonMapper.entityToResponse(null));
        }

    }




    
}
