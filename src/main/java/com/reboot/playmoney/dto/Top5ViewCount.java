package com.reboot.playmoney.dto;

import com.reboot.playmoney.domain.Video;
import com.reboot.playmoney.domain.VideoViewStats;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class Top5ViewCount {

    private Long videoNumber;


    private LocalDate startDate;


    private LocalDate endDate;

    private VideoViewStats.Category category;

    private int viewCount;

    @Builder
    public Top5ViewCount(Long videoNumber, LocalDate startDate, LocalDate endDate, VideoViewStats.Category category, int viewCount) {
        this.videoNumber = videoNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
        this.viewCount = viewCount;
    }
}
