package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.RoleRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.FieldInfoErrorMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.RoleRepository;

@Service
public class RoleValidationServiceImpl implements RoleValidationService {

    private final RoleRepository roleRepository;

    public RoleValidationServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldInfoError> validateRoleExists(RoleRequestDto roleDto) {
        List<FieldInfoError> fields = new ArrayList<>();
        String MESSAGE = "role field does not exist in db";

        if(!roleRepository.existsById(roleDto.getId())) fields.add(FieldInfoErrorMapper.classTargetToFieldInfo(roleDto, "id", MESSAGE));
        if(!roleRepository.existsByName(roleDto.getName())) fields.add(FieldInfoErrorMapper.classTargetToFieldInfo(roleDto, "name", MESSAGE));
        
        return fields;
    }

    
}
