package com.reboot.playmoney.dto;

import com.reboot.playmoney.domain.Member;
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
    private int totalViewCount;
    private int duration;

    public Video toEntity(Member member) {
        return Video.builder()
                .title(title)
                .content(content)
                .totalViewCount(totalViewCount)
                .duration(duration)
                .member(member)
                .build();
    }
}
