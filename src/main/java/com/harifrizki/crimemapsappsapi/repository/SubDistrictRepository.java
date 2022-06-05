package com.harifrizki.crimemapsappsapi.repository;

import com.harifrizki.crimemapsappsapi.entity.SubDistrictEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubDistrictRepository extends JpaRepository<SubDistrictEntity, UUID> {

    @Query(value = "SELECT * FROM Sub_District WHERE sub_district_name LIKE %?1%", nativeQuery = true)
    Page<SubDistrictEntity> findByName(Pageable pageable, String name);

    @Query(value = "SELECT * FROM Sub_District WHERE province_id = ?1 AND sub_district_name LIKE %?2%", nativeQuery = true)
    Page<SubDistrictEntity> findByProvinceId(Pageable pageable, UUID provinceId, String name);

    @Query(value = "SELECT * FROM Sub_District WHERE city_id = ?1 AND sub_district_name LIKE %?2%", nativeQuery = true)
    Page<SubDistrictEntity> findByCityId(Pageable pageable, UUID cityId, String name);

    @Query(value = "SELECT COUNT(1) FROM Sub_District WHERE DATE_PART('day', created_date) = DATE_PART('day', NOW())", nativeQuery = true)
    int countToday();

    @Query(value = "SELECT COUNT(1) FROM Sub_District WHERE DATE_PART('month', created_date) = DATE_PART('month', NOW())", nativeQuery = true)
    int countMonth();
}
