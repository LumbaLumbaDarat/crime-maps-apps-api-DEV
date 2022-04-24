package com.harifrizki.crimemapsappsapi.repository;

import com.harifrizki.crimemapsappsapi.entity.AdminEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, UUID> {

    @Query(value = "SELECT * FROM Admin WHERE admin_username = ?1", nativeQuery = true)
    AdminEntity findByUsername(String username);

    @Query(value = "SELECT * FROM Admin WHERE admin_name LIKE %?1%", nativeQuery = true)
    Page<AdminEntity> findByName(Pageable pageable, String name);

    @Query(value = "SELECT COUNT(1) FROM Admin WHERE DATE_PART('day', created_date) = DATE_PART('day', NOW())", nativeQuery = true)
    int countToday();

    @Query(value = "SELECT COUNT(1) FROM Admin WHERE DATE_PART('month', created_date) = DATE_PART('month', NOW())", nativeQuery = true)
    int countMonth();
}
