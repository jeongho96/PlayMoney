package com.reboot.playmoney.dto;

import com.reboot.playmoney.domain.User;
import com.reboot.playmoney.domain.Video;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class VideoViewResponse {

  private Long id;
  private User user;
  private String title;
  private String content;
  private LocalDateTime createdAt;
  private int duration;
  private int viewCount;


  public VideoViewResponse(Video video) {
    this.id = video.getId();
    this.user = video.getUser();
    this.title = video.getTitle();
    this.content = video.getContent();
    this.createdAt = video.getCreatedAt();
    this.duration = video.getDuration();
    this.viewCount = video.getTotalViewCount();
  }
}
