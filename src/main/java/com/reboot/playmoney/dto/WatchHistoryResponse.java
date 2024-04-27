package com.reboot.playmoney.dto;


import com.reboot.playmoney.domain.WatchHistory;
import lombok.Getter;

@Getter
public class WatchHistoryResponse {


    private Long id;
    private UserDto user;
    private VideoDto video;
    private int playTime;

    public WatchHistoryResponse(WatchHistory watchHistory) {
        id = watchHistory.getWatchNumber();
        user = new UserDto(watchHistory.getMember());
        video = new VideoDto(watchHistory.getVideo());
        playTime = watchHistory.getPlayTime();
    }
}
