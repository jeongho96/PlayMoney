package com.reboot.playmoney.domain;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "watch_history")
@Getter
@Entity
public class WatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "watch_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @LastModifiedDate
    @Column(name = "play_date", nullable = false)
    private LocalDateTime playDate;


    @Column(name = "play_time", nullable = false)
    @Setter private int playTime;

    @Builder
    public WatchHistory(User user , Video video, int playTime) {
        this.user = user;
        this.video = video;
        this.playDate = LocalDateTime.now();
        this.playTime = playTime;
    }

    public void update(User user, Video video, int playTime) {
        this.user = user;
        this.video = video;
        this.playDate = LocalDateTime.now();
        this.playTime = playTime;
    }

}
