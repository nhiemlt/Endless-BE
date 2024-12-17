package com.datn.endless.repositories;

import com.datn.endless.entities.Productversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Productversion, String> {

    @Query(value = "CALL GetStatistics(:startDate, :endDate)", nativeQuery = true)
    List<Object[]> callGetStatistics(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
