package com.reboot.playmoney.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id", updatable = false)
    private Long id;



    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content")
    private String content;

    @CreatedDate
    @Column(name = "upload_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "total_view_count")
    @Setter
    private int totalViewCount;

    @Column(name = "total_ad_view_count")
    @Setter
    private int totalAdViewCount;

    @Column(name = "duration")
    private int duration;

    @ManyToOne
    @JoinColumn(name = "member_id" ,updatable = false)
    private User user;

    @OneToMany(mappedBy = "video")
    private List<WatchHistory> watchHistories = new ArrayList<>();

    @Builder
    public Video(String title, String content, LocalDateTime createdAt, int totalViewCount, int totalAdViewCount, int duration, User user) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.totalViewCount = totalViewCount;
        this.totalAdViewCount = totalAdViewCount;
        this.duration = duration;
        this.user = user;
    }

    public void update(String title, String content, int viewCount) {
        this.title = title;
        this.content = content;
        this.duration = viewCount;
    }
}
