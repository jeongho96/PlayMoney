package com.reboot.playmoney.dto;


import com.reboot.playmoney.domain.WatchHistory;
import lombok.Getter;

@Getter
public class WatchHistoryResponse {


    private Long id;
    private UserDto member;
    private VideoDto video;
    private int playTime;

    public WatchHistoryResponse(WatchHistory watchHistory) {
        id = watchHistory.getWatchNumber();
        member = new UserDto(watchHistory.getMember());
        video = new VideoDto(watchHistory.getVideo());
        playTime = watchHistory.getPlayTime();
    }
}
