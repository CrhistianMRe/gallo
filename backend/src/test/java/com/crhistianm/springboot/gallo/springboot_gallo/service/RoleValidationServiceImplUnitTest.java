package com.crhistianm.springboot.gallo.springboot_gallo.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.RoleRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
public class RoleValidationServiceImplUnitTest {

    @Mock
    RoleRepository repository;

    @InjectMocks
    RoleValidationServiceImpl roleService;

    @Nested
    class ValidateRoleExistsMethodTest{
        List<FieldInfoError> fields;

        @BeforeEach
        void setUp() {
            fields = new ArrayList<>();
            doAnswer(invo ->{
                return invo.getArgument(0, Long.class).equals(1L);
            }).when(repository).existsById(anyLong());

            doAnswer(invo ->{
                return invo.getArgument(0, String.class).equals("ROLE_ADMIN");
            }).when(repository).existsByName(anyString());
        }

        @Test
        void returnsEmptyListWhenAllRoleFieldsExist() {
            fields = roleService.validateRoleExists(new RoleRequestDto(1L, "ROLE_ADMIN"));
            assertThat(fields).hasSize(0);

            verify(repository, times(1)).existsById(1L);
            verify(repository, times(1)).existsByName("ROLE_ADMIN");
        }

        @Test
        void returnsListWithOnlyIdFieldError(){
            fields = roleService.validateRoleExists(new RoleRequestDto(2L, "ROLE_ADMIN"));
            
            assertThat(fields).hasSize(1);

            assertThat(fields).extracting(FieldInfoError::getName, FieldInfoError::getErrorMessage, FieldInfoError::getValue)
                .contains(tuple("id", "role field does not exist in db", 2L));

            verify(repository, times(1)).existsById(2L);
            verify(repository, times(1)).existsByName("ROLE_ADMIN");
        }

        @Test
        void returnsListWithOnlyNameFieldError(){
            fields = roleService.validateRoleExists(new RoleRequestDto(1L, "ROLE_MANAGER"));

            assertThat(fields).hasSize(1);

            FieldInfoError fieldName = fields.get(0);

            assertThat(fieldName.getName()).isEqualTo("name");
            assertThat(fieldName.getErrorMessage()).isEqualTo("role field does not exist in db");
            assertThat(fieldName.getValue()).isEqualTo("ROLE_MANAGER");


            verify(repository, times(1)).existsById(1L);
            verify(repository, times(1)).existsByName("ROLE_MANAGER");
        }

        @Test
        void returnsListWithBothFieldErrors() {
            fields = roleService.validateRoleExists(new RoleRequestDto(3L, "ROLE_WRONG"));

            assertThat(fields).hasSize(2);

            assertThat(fields).filteredOn(e->e.getName().equals("id")).extracting(FieldInfoError::getName, FieldInfoError::getValue)
                .contains(tuple("id", 3L));

            assertThat(fields).filteredOn(e->e.getName().equals("name")).extracting(FieldInfoError::getName, FieldInfoError::getValue)
                .contains(tuple("name", "ROLE_WRONG"));

            assertThat(fields).extracting(FieldInfoError::getErrorMessage)
                .containsExactlyInAnyOrder("role field does not exist in db", "role field does not exist in db");

            verify(repository, times(1)).existsById(3L);
            verify(repository, times(1)).existsByName("ROLE_WRONG");
        }


    }
    
}
