package com.reboot.playmoney.domain;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "watch_history")
@Getter
@Entity
public class WatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "watch_number")
    private Long watchNumber;

    @ManyToOne
    @JoinColumn(name = "member_number")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "video_number")
    private Video video;


    @Column(name = "play_time", nullable = false)
    @Setter private int playTime;

    // 광고수 중복을 피하기 위한 카운트.
    @Column(name = "ad_count")
    @Setter
    private int adCount;

    @Builder
    public WatchHistory(Member member, Video video, int playTime) {
        this.member = member;
        this.video = video;
        this.playTime = playTime;
    }

    public void update(Member member, Video video, int playTime) {
        this.member = member;
        this.video = video;
        this.playTime = playTime;
    }

}
