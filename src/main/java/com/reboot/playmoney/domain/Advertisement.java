package com.reboot.playmoney.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "advertisement")
@Getter
@Setter
public class Advertisement {
    @ManyToOne
    @JoinColumn(name = "video_number", nullable = false)
    private Video video;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ad_number")
    private Long adNumber;

    @Column(name = "total_ad_view_count",nullable = false)
    private int totalAdViewCount;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private int priority;

    public Advertisement(Video video, LocalDateTime now, int priority) {
        this.video = video;
        this.totalAdViewCount = 0;
        this.createdAt = now;
        this.priority = priority;

    }

    public void increaseTotalAdViewCount() {
        this.totalAdViewCount++;
    }

}