package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.RoleRequestDto;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.FieldInfoErrorMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.model.FieldInfoError;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.RoleRepository;

@Service
public class RoleValidationServiceImpl implements RoleValidationService {

    private final RoleRepository roleRepository;

    private final Environment env;

    public RoleValidationServiceImpl(RoleRepository roleRepository, Environment env) {
        this.roleRepository = roleRepository;
        this.env = env;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldInfoError> validateRoleExists(RoleRequestDto roleDto) {
        List<FieldInfoError> fields = new ArrayList<>();

        if(!roleRepository.existsById(roleDto.getId())) {
            fields.add(FieldInfoErrorMapper.classTargetToFieldInfo(roleDto, "id", env.getProperty("role.validation.RoleExists")));
        }
        if(!roleRepository.existsByName(roleDto.getName())) {
            fields.add(FieldInfoErrorMapper.classTargetToFieldInfo(roleDto, "name", env.getProperty("role.validation.RoleExists")));
        }
        
        return fields;
    }

    
}
