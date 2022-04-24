package com.harifrizki.crimemapsappsapi.repository;

import com.harifrizki.crimemapsappsapi.entity.ProvinceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProvinceRepository extends JpaRepository<ProvinceEntity, UUID> {

    @Query(value = "SELECT * FROM Province WHERE province_name LIKE %?1%", nativeQuery = true)
    Page<ProvinceEntity> findByName(Pageable pageable, String name);

    @Query(value = "SELECT COUNT(1) FROM Province WHERE DATE_PART('day', created_date) = DATE_PART('day', NOW())", nativeQuery = true)
    int countToday();

    @Query(value = "SELECT COUNT(1) FROM Province WHERE DATE_PART('month', created_date) = DATE_PART('month', NOW())", nativeQuery = true)
    int countMonth();
}
