package com.reboot.playmoney.repository;

import com.reboot.playmoney.domain.DayCategory;
import com.reboot.playmoney.domain.Sales;
import com.reboot.playmoney.domain.Video;
import com.reboot.playmoney.domain.VideoViewStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface SalesRepository extends JpaRepository<Sales, Long> {
    Sales findByVideoAndStartDateAndEndDateAndCategory
            (Video video, LocalDate startDate, LocalDate endDate, DayCategory category);
}
