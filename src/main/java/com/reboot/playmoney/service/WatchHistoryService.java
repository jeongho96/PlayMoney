package com.reboot.playmoney.service;

import com.reboot.playmoney.domain.*;
import com.reboot.playmoney.dto.WatchHistoryResponse;
import com.reboot.playmoney.repository.AdViewStatsRepository;
import com.reboot.playmoney.repository.VideoRepository;
import com.reboot.playmoney.repository.VideoViewStatsRepository;
import com.reboot.playmoney.repository.WatchHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class WatchHistoryService {

    private final WatchHistoryRepository watchHistoryRepository;
    private final VideoRepository videoRepository;
    private final VideoViewStatsRepository videoViewStatsRepository;
    private final AdViewStatsRepository adViewStatsRepository;


    @Transactional
    public WatchHistoryResponse playVideo(Member member, Video video, int playTime) {

        WatchHistory watchHistory = watchHistoryRepository.findByMember_MemberNumberAndVideo_VideoNumber(
                member.getMemberNumber(), video.getVideoNumber())
                .orElse(new WatchHistory(member, video, 0));

        int totalPlayTime = playTime + watchHistory.getPlayTime();

        // 누적시청시간이 영상 길이보다는 짧고, 영상을 100초 시청하거나,
        // 영상을 끝까지 보는 경우 조회수가 상승.
        if (((playTime >= 100) && (totalPlayTime < video.getDuration())) || (totalPlayTime > video.getDuration())) {
            // 비디오에 대한 카운트는 동시성 체크를 위해서 별도로 미리 누적.
            log.info("increaseTotalViewCount called");
            video.increaseTotalViewCount();
            videoRepository.save(video);
            log.info("Video saved with new total view count:{}", video.getTotalViewCount());

            // 일일 조회수 1씩 누적.
            VideoViewStats videoViewStats = videoViewStatsRepository.findByVideo_VideoNumberAndStartDate(
                    video.getVideoNumber(), LocalDate.now())
                    .orElse(new VideoViewStats(video, 0));

            videoViewStats.increaseViewCount();
            videoViewStatsRepository.save(videoViewStats);
        }

        // 광고 조회수 증가 로직
        // adCount는 watchHistory에 저장되며, 광고 카운트가 중복되지 않도록 해줌.
        int adCount = totalPlayTime / 300;
        if (adCount > watchHistory.getAdCount()) {
            Advertisement ad = video.getAdvertisement();
            ad.increaseTotalAdViewCount();
            watchHistory.setAdCount(adCount);

            AdViewStats adViewStats = adViewStatsRepository.findByAd_AdNumberAndStartDate(ad.getAdNumber(), LocalDate.now())
                    .orElse(new AdViewStats(ad, 0));
            adViewStats.increaseAdViewCount();
            adViewStatsRepository.save(adViewStats);
        }

        // 최근 시청 지점은 그 전 시청 시간.
        // 누적 시청 시간(최근 시청 지점 + 이후 시청 시간)이 동영상 길이를 넘어가면 0으로 초기화.
        if (watchHistory.getPlayTime() + playTime > video.getDuration()) {
            watchHistory.setPlayTime(0);
        }
        else{
            watchHistory.setPlayTime(watchHistory.getPlayTime() + playTime);
        }

        watchHistoryRepository.save(watchHistory);
        videoRepository.save(video);

        return new WatchHistoryResponse(watchHistory);
    }



    public List<WatchHistory> findAll(){
        return watchHistoryRepository.findAll();
    }


    // 기존 WatchHistory 엔티티 업데이트
    @Transactional
    public WatchHistory updatePlayTime(Long id, int playTime) {
        WatchHistory watchHistory = watchHistoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No WatchHistory found with id: " + id));

        watchHistory.setPlayTime(playTime);
        return watchHistory; // JPA의 save 메소드는 엔티티가 이미 존재하면 업데이트를 수행
    }
}
