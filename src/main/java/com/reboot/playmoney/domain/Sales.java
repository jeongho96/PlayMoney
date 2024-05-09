package com.reboot.playmoney.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;


@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "sales")
@Getter
@NoArgsConstructor
public class Sales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_number")
    private Long saleNumber;

    @ManyToOne
    @JoinColumn(name = "member_number", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "video_number", nullable = false)
    private Video video;

    @Setter
    @Column(name = "video_sale_amount",nullable = false)
    private float videoSaleAmount;

    @Setter
    @Column(name = "ad_sale_amount", nullable = false)
    private float adSaleAmount;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('DAY', 'WEEK', 'MONTH')", nullable = false)
    private VideoViewStats.Category category;

    @Column(name = "start_date" , updatable = false)
    private LocalDate startDate;

    @Column(name = "end_date" , updatable = false)
    private LocalDate endDate;

    @Builder
    public Sales(Member member, Video video, float videoSaleAmount, float adSaleAmount, VideoViewStats.Category category, LocalDate startDate, LocalDate endDate) {
        this.member = member;
        this.video = video;
        this.videoSaleAmount = videoSaleAmount;
        this.adSaleAmount = adSaleAmount;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    // getters and setters
}