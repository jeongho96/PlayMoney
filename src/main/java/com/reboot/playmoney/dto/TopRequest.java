package com.reboot.playmoney.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TopRequest {

    private String type;

    private LocalDate startDate;

    private LocalDate endDate;

    public TopRequest(String type, LocalDate startDate, LocalDate endDate) {
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
