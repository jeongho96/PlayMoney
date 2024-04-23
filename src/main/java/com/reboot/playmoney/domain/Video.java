package com.reboot.playmoney.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id", updatable = false)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content")
    private String content;

    @CreatedDate
    @Column(name = "upload_date")
    private LocalDateTime createdAt;

    @Column(name = "view_count")
    private int viewCount;

    @Column(name = "duration")
    private int duration;

    @Builder
    public Video(String title, String content, LocalDateTime createdAt, int viewCount, int duration, Long memberId) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.viewCount = viewCount;
        this.duration = duration;
        this.memberId = memberId;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
