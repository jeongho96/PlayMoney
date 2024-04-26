package com.reboot.playmoney.dto;

import com.reboot.playmoney.domain.User;
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
    private int duration;

    public Video toEntity(User user) {
        return Video.builder()
                .title(title)
                .content(content)
                .viewCount(viewCount)
                .duration(duration)
                .user(user)
                .build();
    }
}
