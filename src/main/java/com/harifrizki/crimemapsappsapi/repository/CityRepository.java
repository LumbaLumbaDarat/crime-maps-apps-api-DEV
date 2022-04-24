package com.harifrizki.crimemapsappsapi.repository;

import com.harifrizki.crimemapsappsapi.entity.CityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, UUID> {

    @Query(value = "SELECT * FROM City WHERE city_name LIKE %?1%", nativeQuery = true)
    Page<CityEntity> findByName(Pageable pageable, String name);

    @Query(value = "SELECT * FROM City WHERE province_id = ?1 AND city_name LIKE %?2%", nativeQuery = true)
    Page<CityEntity> findByProvinceId(Pageable pageable, UUID provinceId, String name);

    @Query(value = "SELECT COUNT(1) FROM City WHERE DATE_PART('day', created_date) = DATE_PART('day', NOW())", nativeQuery = true)
    int countToday();

    @Query(value = "SELECT COUNT(1) FROM City WHERE DATE_PART('month', created_date) = DATE_PART('month', NOW())", nativeQuery = true)
    int countMonth();
}
