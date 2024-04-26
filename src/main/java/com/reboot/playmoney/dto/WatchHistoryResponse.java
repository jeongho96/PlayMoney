package com.reboot.playmoney.dto;


import com.reboot.playmoney.domain.User;
import com.reboot.playmoney.domain.Video;
import com.reboot.playmoney.domain.WatchHistory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class WatchHistoryResponse {


    private Long id;
    private UserDto user;
    private VideoDto video;
    private int playTime;

    public WatchHistoryResponse(WatchHistory watchHistory) {
        id = watchHistory.getId();
        user = new UserDto(watchHistory.getUser());
        video = new VideoDto(watchHistory.getVideo());
        playTime = watchHistory.getPlayTime();
    }
}
