package com.reboot.playmoney.dto;

import com.reboot.playmoney.domain.Video;
import lombok.Getter;


@Getter
public class VideoResponse {

    private final String title;
    private final String content;
    private final int duration;
    private final int viewCount;


    public VideoResponse(Video video) {
        this.title = video.getTitle();
        this.content = video.getContent();
        this.duration = video.getDuration();
        this.viewCount = video.getViewCount();
    }
}
