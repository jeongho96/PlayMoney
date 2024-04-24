package com.reboot.playmoney.domain;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "watchHistory")
@Getter
@Entity
public class WatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "watch_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @LastModifiedDate
    @Column(name = "play_date", nullable = false)
    private LocalDateTime playDate;


    @Column(name = "play_time", nullable = false)
    @Setter private int playTime;

    @Builder
    public WatchHistory(Long memberId, Long videoId, int lastWatch, int playTime) {
        this.memberId = memberId;
        this.videoId = videoId;
        this.playDate = LocalDateTime.now();
        this.playTime = playTime;
    }

    public void update(Long memberId, Long videoId, int playTime) {
        this.memberId = memberId;
        this.videoId = videoId;
        this.playDate = LocalDateTime.now();
        this.playTime = playTime;
    }

}
