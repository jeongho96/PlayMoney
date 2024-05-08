package com.reboot.playmoney.controller;


import com.reboot.playmoney.domain.Video;
import com.reboot.playmoney.domain.VideoViewStats;
import com.reboot.playmoney.dto.Top5ViewCount;
import com.reboot.playmoney.dto.TopRequest;
import com.reboot.playmoney.dto.VideoListViewResponse;
import com.reboot.playmoney.repository.VideoRepository;
import com.reboot.playmoney.repository.VideoViewStatsRepository;
import com.reboot.playmoney.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StatisticsController {

    private final StatisticsService statisticsService;

    private final VideoViewStatsRepository videoViewStatsRepository;
    private final VideoRepository videoRepository;

    @GetMapping("/top5-view")
    public ResponseEntity<List<Top5ViewCount>> findViewTop5Video(@RequestBody TopRequest topRequest) {
        log.info("통계 조회 수 Top5 조회");

        List<Top5ViewCount> top5Dto = statisticsService.findViewTop5Video(topRequest);
        return ResponseEntity.ok(top5Dto);
    }

    @GetMapping("/top5-video")
    public ResponseEntity<List<VideoListViewResponse>> findDurationTop5Video(@RequestBody TopRequest topRequest) {
        log.info("통계 영상 길이 Top5 조회");

        LocalDateTime startTime = topRequest.getStartDate().atStartOfDay();

        LocalDateTime endTime = topRequest.getEndDate().atTime(23, 59, 59);

        List<VideoListViewResponse> top5Dto = statisticsService.findDurationTop5Video(startTime, endTime);
        return ResponseEntity.ok(top5Dto);
    }


    @GetMapping("/test-video")
    public ResponseEntity<Boolean> checkMethodVideo() {
        LocalDate startDate = LocalDate.now().minusDays(6).with(DayOfWeek.MONDAY);
        LocalDate endDate = startDate.plusDays(6);
        Video video = videoRepository.findById(2L).orElse(null);


        return ResponseEntity.ok(
                videoViewStatsRepository.existsByVideoAndCategoryAndStartDateAndEndDate(video, VideoViewStats.Category.WEEK,startDate,endDate)
        );
    }
}
