package com.reboot.playmoney.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;


@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "sales")
@Getter
@NoArgsConstructor
class Sales {
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

    @Column(name = "video_sale_amount",nullable = false)
    private BigDecimal videoSaleAmount;

    @Column(name = "ad_sale_amount", nullable = false)
    private BigDecimal adSaleAmount;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate createdAt;

    // getters and setters
}