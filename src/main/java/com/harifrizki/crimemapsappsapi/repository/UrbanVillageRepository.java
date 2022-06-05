package com.harifrizki.crimemapsappsapi.repository;

import com.harifrizki.crimemapsappsapi.entity.UrbanVillageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UrbanVillageRepository extends JpaRepository<UrbanVillageEntity, UUID> {

    @Query(value = "SELECT * FROM Urban_Village WHERE urban_village_name LIKE %?1%", nativeQuery = true)
    Page<UrbanVillageEntity> findByName(Pageable pageable, String name);

    @Query(value = "SELECT * FROM Urban_Village WHERE province_id = ?1 AND urban_village_name LIKE %?2%", nativeQuery = true)
    Page<UrbanVillageEntity> findByProvinceId(Pageable pageable, UUID provinceId, String name);

    @Query(value = "SELECT * FROM Urban_Village WHERE city_id = ?1 AND urban_village_name LIKE %?2%", nativeQuery = true)
    Page<UrbanVillageEntity> findByCityId(Pageable pageable, UUID cityId, String name);

    @Query(value = "SELECT * FROM Urban_Village WHERE sub_district_id = ?1 AND urban_village_name LIKE %?2%", nativeQuery = true)
    Page<UrbanVillageEntity> findBySubDistrictId(Pageable pageable, UUID subDistrictId, String name);

    @Query(value = "SELECT COUNT(1) FROM Urban_Village WHERE DATE_PART('day', created_date) = DATE_PART('day', NOW())", nativeQuery = true)
    int countToday();

    @Query(value = "SELECT COUNT(1) FROM Urban_Village WHERE DATE_PART('month', created_date) = DATE_PART('month', NOW())", nativeQuery = true)
    int countMonth();
}
