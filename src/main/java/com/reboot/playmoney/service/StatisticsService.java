package com.reboot.playmoney.service;

import com.reboot.playmoney.domain.Video;
import com.reboot.playmoney.domain.VideoViewStats;
import com.reboot.playmoney.dto.Top5ViewCount;
import com.reboot.playmoney.dto.TopRequest;
import com.reboot.playmoney.dto.VideoListViewResponse;
import com.reboot.playmoney.repository.VideoRepository;
import com.reboot.playmoney.repository.VideoViewStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final VideoViewStatsRepository videoViewStatsRepository;
    private final VideoRepository videoRepository;



    public List<Top5ViewCount> findViewTop5Video(TopRequest topRequest) {

        List<Object[]> top5Stats;


        if (topRequest.getType().equals("DAY")) {
            top5Stats = videoViewStatsRepository.findTop5ViewStatsByPeriod(topRequest.getStartDate(), topRequest.getStartDate());
        } else {
            top5Stats = videoViewStatsRepository.findTop5ViewStatsByPeriod(topRequest.getStartDate(), topRequest.getEndDate());
        }

        List<Top5ViewCount> top5ViewCounts = new ArrayList<>();

        for (Object[] result : top5Stats) {
            Long videoNumber = (Long) result[0];
            int viewCount = ((Number) result[1]).intValue();

            Video video = videoRepository.findById(videoNumber).orElse(null);

            // Check if video exists
            if (video != null) {
                Top5ViewCount top5ViewCount = Top5ViewCount.builder()
                        .videoNumber(video.getVideoNumber())
                        .startDate(topRequest.getStartDate())
                        .endDate(topRequest.getEndDate())
                        .category(VideoViewStats.Category.valueOf(topRequest.getType()))
                        .viewCount(viewCount)
                        .build();

                top5ViewCounts.add(top5ViewCount);
            }

        }
        return top5ViewCounts;
    }

    public List<VideoListViewResponse> findDurationTop5Video(LocalDateTime startDate, LocalDateTime endDate) {
        List<Video> video = videoRepository.findTop5VideosByDurationBetweenDates(startDate, endDate);

        List<VideoListViewResponse> videoListViewResponses = new ArrayList<>();
        for (Video video1 : video) {
            videoListViewResponses.add(new VideoListViewResponse(video1));
        }

        return videoListViewResponses;
    }
}
