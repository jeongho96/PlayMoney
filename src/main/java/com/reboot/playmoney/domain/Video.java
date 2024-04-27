package com.reboot.playmoney.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "video")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_number", updatable = false)
    private Long videoNumber;

    @ManyToOne
    @JoinColumn(name = "member_number" ,updatable = false)
    private Member member;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content")
    private String content;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "total_view_count")
    @Setter
    private int totalViewCount;


    @Column(name = "duration")
    private int duration;

    @OneToMany(mappedBy = "video")
    private List<Advertisement> advertisements = new ArrayList<>();

    @OneToMany(mappedBy = "video")
    private List<WatchHistory> watchHistories = new ArrayList<>();

    @Builder
    public Video(String title, String content, LocalDateTime createdAt, int totalViewCount, int duration, Member member) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.totalViewCount = totalViewCount;
        this.duration = duration;
        this.member = member;
    }

    public void update(String title, String content, int viewCount) {
        this.title = title;
        this.content = content;
        this.duration = viewCount;
    }

    public void increaseTotalViewCount() {
        this.totalViewCount++;
    }

    public Advertisement getAdvertisement() {
        return this.advertisements.stream()
                .min(Comparator.comparingInt(Advertisement::getPriority))
                .orElse(null);
    }

}
