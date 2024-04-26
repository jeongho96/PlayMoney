package com.reboot.playmoney.dto;

import com.reboot.playmoney.domain.Video;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddVideoRequest {

    private String title;
    private String content;
    private int viewCount;
    private Long memberId;
    private int duration;

    public Video toEntity(Long memberId) {
        return Video.builder()
                .title(title)
                .content(content)
                .memberId(memberId)
                .build();
    }
}