package com.sibrahim.annoncify.controller;

import com.sibrahim.annoncify.dto.ResponseMessage;
import com.sibrahim.annoncify.repository.RegionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/regions")
public class RegionController {
    private final RegionRepository regionRepository;

    public RegionController(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @GetMapping
    public ResponseEntity<ResponseMessage> getRegions() {
        return ResponseEntity.ok(ResponseMessage.builder()
                        .message("Regions retrieved successfully")
                        .status(200)
                        .data(regionRepository.findAll())
                .build());
    }
}
