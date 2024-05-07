package com.reboot.playmoney.repository;

import com.reboot.playmoney.domain.VideoViewStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VideoViewStatsRepository extends JpaRepository<VideoViewStats, Long> {
    Optional<VideoViewStats> findByVideo_VideoNumberAndStartDate(Long videoNumber, LocalDate now);

    @Query("SELECT v.video.videoNumber, SUM(v.viewCount) AS view_count " +
            "FROM VideoViewStats v " +
            "WHERE v.startDate BETWEEN :startDate AND :endDate " +
            "AND v.endDate BETWEEN :startDate AND :endDate " +
            "GROUP BY v.video.videoNumber " +
            "ORDER BY view_count DESC LIMIT 5")
    List<Object[]> findTop5ViewStatsByPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);



}
