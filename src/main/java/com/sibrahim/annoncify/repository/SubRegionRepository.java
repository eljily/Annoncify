package com.sibrahim.annoncify.repository;

import com.sibrahim.annoncify.entity.Region;
import com.sibrahim.annoncify.entity.SubRegion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubRegionRepository extends JpaRepository<SubRegion, Long> {
    Optional<SubRegion> findByRegionAndName(Region region, String name);
}
