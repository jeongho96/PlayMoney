package com.reboot.playmoney.dto;

import com.reboot.playmoney.domain.Video;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class VideoViewResponse {

  private Long id;
  private Long memberId;
  private String title;
  private String content;
  private LocalDateTime createdAt;
  private int duration;


  public VideoViewResponse(Video video) {
    this.id = video.getId();
    this.memberId = video.getMemberId();
    this.title = video.getTitle();
    this.content = video.getContent();
    this.createdAt = video.getCreatedAt();
    this.duration = video.getDuration();
  }
}
