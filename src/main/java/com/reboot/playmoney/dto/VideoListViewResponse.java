package com.reboot.playmoney.dto;

import com.reboot.playmoney.domain.Video;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class VideoListViewResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final int viewCount;
    private final int duration;

    public VideoListViewResponse(Video video) {
        this.id = video.getId();
        this.title = video.getTitle();
        this.content = video.getContent();
        this.createdAt = video.getCreatedAt();
        this.viewCount = video.getViewCount();
        this.duration = video.getDuration();
    }
}
