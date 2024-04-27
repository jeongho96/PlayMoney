package com.reboot.playmoney.repository;

import com.reboot.playmoney.domain.VideoViewStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface VideoViewStatsRepository extends JpaRepository<VideoViewStats, Long> {
    Optional<VideoViewStats> findByVideo_VideoNumberAndCreatedAt(Long videoNumber, LocalDate now);
}
