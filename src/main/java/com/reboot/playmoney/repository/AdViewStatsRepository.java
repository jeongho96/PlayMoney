package com.reboot.playmoney.repository;

import com.reboot.playmoney.domain.AdViewStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AdViewStatsRepository extends JpaRepository<AdViewStats, Long> {
    Optional<AdViewStats> findByAd_AdNumberAndStartDate(Long adNumber, LocalDate now);
}
