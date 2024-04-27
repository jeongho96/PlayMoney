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

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate createdAt;



    @Column(name = "ad_view_count")
    private int adViewCount;

    @Builder
    public AdViewStats(Advertisement ad, int adViewCount) {
        this.ad = ad;
        this.createdAt = LocalDate.now();
        this.adViewCount = adViewCount;


    }

    public void increaseAdViewCount() {
        this.adViewCount++;
    }


}
