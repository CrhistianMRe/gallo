package com.crhistianm.springboot.gallo.springboot_gallo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crhistianm.springboot.gallo.springboot_gallo.dto.BodyPartResponseDto;
import com.crhistianm.springboot.gallo.springboot_gallo.mapper.BodyPartMapper;
import com.crhistianm.springboot.gallo.springboot_gallo.repository.BodyPartRepository;

@Service
public class BodyPartService {

    private final BodyPartRepository bodyPartRepository;

    public BodyPartService(BodyPartRepository bodyPartRepository) {
        this.bodyPartRepository = bodyPartRepository;
    }

    @Transactional(readOnly = true)
    public List<BodyPartResponseDto> getAll() {
        return bodyPartRepository.findAll().stream().map(BodyPartMapper::entityToResponse).collect(Collectors.toList());
    }

}

