package com.reboot.playmoney.domain;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;


@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "ad_view_stats")
public class AdViewStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "view_number")
    private Long viewId;

    @ManyToOne
    @JoinColumn(name = "ad_number")
    private Advertisement ad;

    @Column(name = "start_date" , updatable = false)
    private LocalDate startDate;

    @Column(name = "end_date" , updatable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('DAY', 'WEEK', 'MONTH')", nullable = false)
    private VideoViewStats.Category category;

    @Column(name = "ad_view_count")
    private int adViewCount;

    @Builder
    public AdViewStats(Advertisement ad, int adViewCount) {
        this.ad = ad;
        this.startDate = LocalDate.now();
        this.endDate = LocalDate.now();
        this.adViewCount = adViewCount;
        this.category = VideoViewStats.Category.DAY;


    }

    public void increaseAdViewCount() {
        this.adViewCount++;
    }

    public enum Category {
        DAY,
        WEEK,
        MONTH
    }
}
