package com.reboot.playmoney.dto;

import com.reboot.playmoney.domain.Video;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VideoDto {
    private Long id;
    private String title;

    public VideoDto(Video video) {
        this.id = video.getId();
        this.title = video.getTitle();
    }

    // getters
}