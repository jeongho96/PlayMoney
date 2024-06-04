package com.reboot.playmoney.repository;

import com.reboot.playmoney.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SalesRepository extends JpaRepository<Sales, Long> {
    Sales findByVideoAndStartDateAndEndDateAndCategory
            (Video video, LocalDate startDate, LocalDate endDate, DayCategory category);

    Sales findFirstByVideoAndStartDateAndEndDateAndCategory(
            Video video, LocalDate startDate, LocalDate endDate, DayCategory category);
    List<Sales> findByMember(Member member);
}
