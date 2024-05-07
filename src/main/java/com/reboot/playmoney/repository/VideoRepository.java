package com.reboot.playmoney.repository;


import com.reboot.playmoney.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    @Query("SELECT v FROM Video v WHERE v.createdAt " +
            "BETWEEN :startDate AND :endDate " +
            "ORDER BY v.duration DESC LIMIT 5")
    List<Video> findTop5VideosByDurationBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}

