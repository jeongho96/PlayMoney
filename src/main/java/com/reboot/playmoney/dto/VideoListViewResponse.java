package com.reboot.playmoney.dto;

import com.reboot.playmoney.domain.Video;
import lombok.Getter;


@Getter
public class VideoListViewResponse {

    private final Long id;
    private final String title;
    private final String content;

    public VideoListViewResponse(Video video) {
        this.id = video.getId();
        this.title = video.getTitle();
        this.content = video.getContent();
    }
}
