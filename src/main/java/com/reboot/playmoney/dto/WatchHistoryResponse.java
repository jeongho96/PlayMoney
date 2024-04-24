package com.reboot.playmoney.dto;


import com.reboot.playmoney.domain.WatchHistory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class WatchHistoryResponse {


    private Long id;
    private Long memberId;
    private Long videoId;
    private LocalDateTime playDate;
    private int playTime;

    public WatchHistoryResponse(WatchHistory watchHistory) {
        id = watchHistory.getId();
        memberId = watchHistory.getMemberId();
        videoId = watchHistory.getVideoId();
        playDate = watchHistory.getPlayDate();
        playTime = watchHistory.getPlayTime();
    }
}
