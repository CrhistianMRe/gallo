package com.crhistianm.springboot.gallo.springboot_gallo.bodypart;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class BodyPartService {

    private final BodyPartRepository bodyPartRepository;

    BodyPartService(BodyPartRepository bodyPartRepository) {
        this.bodyPartRepository = bodyPartRepository;
    }

    @Transactional(readOnly = true)
    List<BodyPartResponseDto> getAll() {
        return bodyPartRepository.findAll().stream().map(BodyPartMapper::entityToResponse).collect(Collectors.toList());
    }

}

