package com.reboot.playmoney.batch;

import com.reboot.playmoney.domain.*;
import com.reboot.playmoney.repository.SalesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyAdSalesProcessor implements ItemProcessor<AdViewStats, Sales> {

    private final SalesRepository salesRepository;

    ConcurrentHashMap<Long, Sales> dailyAdViewCache = new ConcurrentHashMap<>();


    @Override
    public Sales process(AdViewStats dailyStats) throws Exception {

        float adSaleAmount = getAdSaleAmount(dailyStats);

        // 정산 순서는 video View를 무조건 먼저 수행하므로 없는 경우는 에러가 발생했으므로 null.
        Sales exists = salesRepository.findByVideoAndStartDateAndEndDateAndCategory(
                dailyStats.getAd().getVideo(),
                dailyStats.getStartDate(),
                dailyStats.getEndDate(),
                dailyStats.getCategory()
        );
        if (exists == null){
            return null;
        }

        Video video = dailyStats.getAd().getVideo();
        final Long videoNumber = video.getVideoNumber();
        Member member = video.getMember();

        Sales alreadyExists = dailyAdViewCache.compute(videoNumber, (key, sales) -> {
                if (sales == null) {
                    return Sales.builder()
                            .member(member)
                            .video(dailyStats.getAd().getVideo())
                            .startDate(dailyStats.getStartDate())
                            .endDate(dailyStats.getStartDate()) // 일일의 경우 날짜는 동일.
                            .category(DayCategory.DAY)
                            .adSaleAmount(adSaleAmount)
                            .build();

                } else {
                    sales.setAdSaleAmount(sales.getAdSaleAmount() + adSaleAmount);
                    log.info("누적 sales : {}", sales.getAdSaleAmount());
                    return sales;
                }
            });

        log.info("단일 Ad Sales Amount : {}", adSaleAmount);
        log.info("기본 Map 저장 SalesAmount : {}", alreadyExists.getAdSaleAmount());
        log.info("total Ad Sale Amount: {}", alreadyExists.getAdSaleAmount());

        exists.setAdSaleAmount(alreadyExists.getAdSaleAmount());

        return exists;
    }

    private static float getAdSaleAmount(AdViewStats adViewStats) {
        int viewCount = adViewStats.getAdViewCount();
        float videoSaleAmount = 0;

        // 구간별 단가 적용
        if (viewCount > 1000000) {
            videoSaleAmount += (viewCount - 1000000) * 20;
            viewCount = 1000000;
            log.info("100만 구간 정산가 : {}", videoSaleAmount);
        }
        if (viewCount > 500000) {
            videoSaleAmount += (viewCount - 500000) * 15;
            viewCount = 500000;
            log.info("50만 구간 정산가 : {}", videoSaleAmount);
        }
        if (viewCount > 100000) {
            videoSaleAmount += (viewCount - 100000) * 12;
            viewCount = 100000;
            log.info("10만 구간 정산가 : {}", videoSaleAmount);
        }
        videoSaleAmount += viewCount * 10;
        log.info("정산가 : {}", videoSaleAmount);
        return videoSaleAmount;
    }

}
