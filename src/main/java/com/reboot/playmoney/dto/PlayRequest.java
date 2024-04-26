package com.reboot.playmoney.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayRequest {
    private Long videoId;
    private int playTime;

    // getters and setters
}
