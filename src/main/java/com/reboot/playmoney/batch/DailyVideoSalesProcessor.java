package com.reboot.playmoney.batch;

import com.reboot.playmoney.domain.Sales;
import com.reboot.playmoney.domain.VideoViewStats;
import com.reboot.playmoney.repository.SalesRepository;
import com.reboot.playmoney.repository.VideoRepository;
import com.reboot.playmoney.repository.VideoViewStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyVideoSalesProcessor implements ItemProcessor<VideoViewStats, Sales> {

    private final SalesRepository salesRepository;


    private final VideoRepository videoRepository;
    private final VideoViewStatsRepository videoViewStatsRepository;

    @Override
    public Sales process(VideoViewStats videoViewStats) throws Exception {
        float videoSaleAmount = getVideoSaleAmount(videoViewStats);

        Sales exists = salesRepository.findByVideoAndStartDateAndEndDateAndCategory(
                videoViewStats.getVideo(),
                videoViewStats.getStartDate(),
                videoViewStats.getEndDate(),
                videoViewStats.getCategory()
        );

        if (exists == null) {
            return Sales.builder()
                    .member(videoViewStats.getVideo().getMember())
                    .video(videoViewStats.getVideo())
                    .videoSaleAmount(videoSaleAmount)
                    .category(videoViewStats.getCategory())
                    .startDate(videoViewStats.getStartDate())
                    .endDate(videoViewStats.getEndDate())
                    .build();
        } else {
            exists.setVideoSaleAmount(videoSaleAmount);
            return exists;
        }

    }

    private static float getVideoSaleAmount(VideoViewStats videoViewStats) {
        int viewCount = videoViewStats.getViewCount();
        float videoSaleAmount = 0;

        // 구간별 단가 적용
        if (viewCount > 1000000) {
            videoSaleAmount += (viewCount - 1000000) * 1.5f;
            viewCount = 1000000;
            log.info("100만 구간 정산가 : {}", videoSaleAmount);
        }
        if (viewCount > 500000) {
            videoSaleAmount += (viewCount - 500000) * 1.3f;
            viewCount = 500000;
            log.info("50만 구간 정산가 : {}", videoSaleAmount);
        }
        if (viewCount > 100000) {
            videoSaleAmount += (viewCount - 100000) * 1.1f;
            viewCount = 100000;
            log.info("10만 구간 정산가 : {}", videoSaleAmount);
        }
        log.info("정산가 : {}", videoSaleAmount);
        videoSaleAmount += viewCount * 1;
        return videoSaleAmount;
    }

}
