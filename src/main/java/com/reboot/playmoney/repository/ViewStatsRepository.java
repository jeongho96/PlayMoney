package com.reboot.playmoney.repository;

import com.reboot.playmoney.domain.ViewStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ViewStatsRepository extends JpaRepository<ViewStats, Long> {
    Optional<ViewStats> findByVideo_IdAndCreatedAt(Long video_id, LocalDate createdAt);
}
