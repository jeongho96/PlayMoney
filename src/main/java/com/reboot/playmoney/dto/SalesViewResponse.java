package com.reboot.playmoney.dto;


import com.reboot.playmoney.domain.DayCategory;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
public class SalesViewResponse {

    // 비디오 : 정산금액, 광고 정산금액.
    // 비디오 id, 이름

    private Long videoNumber;

    private String videoTitle;

    private float videoSaleAmount;

    private float adSaleAmount;

    private DayCategory category;

    private LocalDate startDate;

    private LocalDate endDate;


    @Builder
    public SalesViewResponse(Long videoNumber, String videoTitle, float videoSaleAmount,
                             float adSaleAmount, DayCategory category, LocalDate startDate,
                             LocalDate endDate) {
        this.videoNumber = videoNumber;
        this.videoTitle = videoTitle;
        this.videoSaleAmount = videoSaleAmount;
        this.adSaleAmount = adSaleAmount;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
