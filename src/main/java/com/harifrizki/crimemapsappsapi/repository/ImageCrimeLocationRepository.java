package com.harifrizki.crimemapsappsapi.repository;

import com.harifrizki.crimemapsappsapi.entity.ImageCrimeLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ImageCrimeLocationRepository extends JpaRepository<ImageCrimeLocationEntity, UUID> {
}
