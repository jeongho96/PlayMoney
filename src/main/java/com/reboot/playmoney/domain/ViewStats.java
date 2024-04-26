package com.reboot.playmoney.domain;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;


@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "view_stats")
public class ViewStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long viewId;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate createdAt;

    private int viewCount;
    private int adViewCount;

    @Builder
    public ViewStats(Video video,  int viewCount, int adViewCount) {
        this.video = video;
        this.createdAt = LocalDate.now();
        this.viewCount = viewCount;
        this.adViewCount = adViewCount;
    }

}
