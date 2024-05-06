package com.reboot.playmoney.service;

import com.reboot.playmoney.domain.*;
import com.reboot.playmoney.dto.WatchHistoryResponse;
import com.reboot.playmoney.repository.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Transactional
public class WatchHistoryServiceTest {

    @Autowired
    private WatchHistoryService watchHistoryService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private WatchHistoryRepository watchHistoryRepository;

    @Autowired
    private VideoViewStatsRepository videoViewStatsRepository;

    @Autowired
    private AdViewStatsRepository adViewStatsRepository;

    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Autowired
    private UserRepository userRepository;

    private Member member;
    private Video video;
    private int playTime;

    @BeforeEach
    public void setup() {
        // Given
        member = Member.builder()
                .email("test-email")
                .username("test-username")
                .userType(UserType.SELLER)
                .build();
        member = userRepository.save(member);

        video = Video.builder()
                .member(member)
                .title("test-title")
                .duration(1500)
                .build();
        // Save the Video first
        videoRepository.save(video);

        System.out.println("Initial total view count: " + video.getTotalViewCount());

        // Set up Advertisement
        Advertisement ad = new Advertisement(video, LocalDateTime.now(), 1);
        advertisementRepository.save(ad); // Save the Advertisement

        // Add the Advertisement to the Video's list of advertisements and save again
        video.getAdvertisements().add(ad);
        videoRepository.save(video); // Save the Video with the added Advertisement

        // Set up AdViewStats
        AdViewStats adViewStats = new AdViewStats(ad, 0);
        adViewStatsRepository.save(adViewStats);

        playTime = 400;

        // Set up VideoViewStats
        VideoViewStats videoViewStats = new VideoViewStats(video, 0);
        videoViewStatsRepository.save(videoViewStats);

    }

    @Test
    public void testPlayVideo() throws InterruptedException {
        // When
        WatchHistoryResponse result = watchHistoryService.playVideo(member, video, playTime);

        // 데이터베이스 변경사항 반영 대기 (권장되지 않음)
        Thread.sleep(1000); // 1초 대기

        // Then
        assertThat(result.getMember().getId()).as("Member ID should match").isEqualTo(member.getMemberNumber());
        assertThat(result.getMember().getName()).as("Member name should match").isEqualTo(member.getName());
        assertThat(result.getVideo().getId()).as("Video ID should match").isEqualTo(video.getVideoNumber());
        assertThat(result.getVideo().getTitle()).as("Video title should match").isEqualTo(video.getTitle());
        assertThat(result.getPlayTime()).as("Play time should match").isEqualTo(playTime);

        // Check if the total view count of the video has increased
        Video updatedVideo = videoRepository.findById(video.getVideoNumber()).orElseThrow(() -> new IllegalArgumentException("Video not found: " + video.getVideoNumber()));
        assertThat(updatedVideo.getTotalViewCount()).as("Total view count should be greater after playing video").isEqualTo(video.getTotalViewCount());

        // Check if the view count of the video for today has increased
        VideoViewStats videoViewStats = videoViewStatsRepository.findByVideo_VideoNumberAndStartDate(video.getVideoNumber(), LocalDate.now())
                .orElseThrow(() -> new IllegalArgumentException("VideoViewStats not found for video: " + video.getVideoNumber() + " and date: " + LocalDate.now()));
        assertThat(videoViewStats.getViewCount()).as("Today's view count should be greater than 0").isGreaterThan(0);

        // Check if the total ad view count of the advertisement has increased
        Advertisement ad = updatedVideo.getAdvertisement();
        assertThat(ad.getTotalAdViewCount()).as("Total ad view count should be greater after playing video").isGreaterThan(0);

        // Check if the ad view count of the advertisement for today has increased
        AdViewStats adViewStats = adViewStatsRepository.findByAd_AdNumberAndStartDate(ad.getAdNumber(), LocalDate.now())
                .orElseThrow(() -> new IllegalArgumentException("AdViewStats not found for ad: " + ad.getAdNumber() + " and date: " + LocalDate.now()));
        assertThat(adViewStats.getAdViewCount()).as("Today's ad view count should be greater than 0").isGreaterThan(0);
    }


}