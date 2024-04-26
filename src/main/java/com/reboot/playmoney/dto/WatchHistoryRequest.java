package com.reboot.playmoney.dto;


import com.reboot.playmoney.domain.WatchHistory;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class WatchHistoryRequest {


    @NotNull
    private Long memberId;
    @NotNull
    private Long videoId;
    @Setter private int playTime;


//    public WatchHistory toEntity(Long memberId) {
//        return WatchHistory.builder()
//
//                .videoId(videoId)
//                .playTime(playTime)
//                .build();
//    }
}
