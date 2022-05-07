package com.harifrizki.crimemapsappsapi.repository;

import com.harifrizki.crimemapsappsapi.entity.CrimeLocationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CrimeLocationRepository extends JpaRepository<CrimeLocationEntity, UUID> {

    @Query(value = "SELECT * FROM crime_location WHERE crime_maps_name LIKE %?1%", nativeQuery = true)
    Page<CrimeLocationEntity> findByName(Pageable pageable, String name);

    @Query(value = "SELECT * FROM crime_location WHERE province_id = ?1 AND crime_maps_name LIKE %?2%", nativeQuery = true)
    Page<CrimeLocationEntity> findByProvinceId(Pageable pageable, UUID provinceId, String name);

    @Query(value = "SELECT * FROM crime_location WHERE city_id = ?1 AND crime_maps_name LIKE %?2%", nativeQuery = true)
    Page<CrimeLocationEntity> findByCityId(Pageable pageable, UUID cityId, String name);

    @Query(value = "SELECT * FROM crime_location WHERE sub_district_id = ?1 AND crime_maps_name LIKE %?2%", nativeQuery = true)
    Page<CrimeLocationEntity> findBySubDistrictId(Pageable pageable, UUID subDistrictId, String name);

    @Query(value = "SELECT * FROM crime_location WHERE urban_village_id = ?1 AND crime_maps_name LIKE %?2%", nativeQuery = true)
    Page<CrimeLocationEntity> findByUrbanVillageId(Pageable pageable, UUID urbanVillage, String name);

    @Query(value = "SELECT COUNT(1) FROM crime_location WHERE DATE_PART('day', created_date) = DATE_PART('day', NOW())", nativeQuery = true)
    int countToday();

    @Query(value = "SELECT COUNT(1) FROM crime_location WHERE DATE_PART('month', created_date) = DATE_PART('month', NOW())", nativeQuery = true)
    int countMonth();
}
