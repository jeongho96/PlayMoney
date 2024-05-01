package com.reboot.playmoney.domain;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;


@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "video_view_stats")
public class VideoViewStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "view_number")
    private Long viewNumber;

    @ManyToOne
    @JoinColumn(name = "video_number")
    private Video video;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate createdAt;



    @Column(name = "view_count")
    private int viewCount;

    @Builder
    public VideoViewStats(Video video, int viewCount) {
        this.video = video;
        this.createdAt = LocalDate.now();
        this.viewCount = viewCount;

    }

    public void increaseViewCount() {
        this.viewCount++;
    }

}
