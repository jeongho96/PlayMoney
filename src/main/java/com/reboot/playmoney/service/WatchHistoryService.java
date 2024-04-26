package com.reboot.playmoney.service;

import com.reboot.playmoney.domain.User;
import com.reboot.playmoney.domain.Video;
import com.reboot.playmoney.domain.ViewStats;
import com.reboot.playmoney.domain.WatchHistory;
import com.reboot.playmoney.dto.WatchHistoryResponse;
import com.reboot.playmoney.repository.VideoRepository;
import com.reboot.playmoney.repository.ViewStatsRepository;
import com.reboot.playmoney.repository.WatchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class WatchHistoryService {

    private final WatchHistoryRepository watchHistoryRepository;
    private final VideoRepository videoRepository;
    private final ViewStatsRepository viewStatsRepository;


    @Transactional
    public WatchHistoryResponse playVideo(User user, Video video, int playTime) {
        WatchHistory watchHistory = watchHistoryRepository.findByUser_IdAndVideo_Id(user.getId(), video.getId())
                .orElse(new WatchHistory(user, video, 0));

        int totalPlayTime = playTime + watchHistory.getPlayTime();

        // 누적시청시간이 영상 길이보다는 짧고, 영상을 100초 시청하거나,
        // 영상을 끝까지 보는 경우 조회수가 상승.
        if (((playTime >= 100) && (totalPlayTime < video.getDuration())) || (totalPlayTime > video.getDuration())) {
            video.setTotalViewCount(video.getTotalViewCount() + 1);

            ViewStats viewStats = viewStatsRepository.findByVideo_IdAndCreatedAt(video.getId(), LocalDate.now())
                    .orElse(new ViewStats(video, 0, 0));
            viewStats.setViewCount(viewStats.getViewCount() + 1);
            viewStatsRepository.save(viewStats);
        }

        // 광고 조회수 증가 로직
        int adCount = totalPlayTime / 300;
        if (adCount > watchHistory.getAdCount()) {
            // 추후에 TotalAdViewCount와 TotalViewCount는 스케줄러를 활용해서 누적해야될 수도 있음.(실시간 반영이 어렵다면)
            video.setTotalAdViewCount(video.getTotalAdViewCount() + 1);
            watchHistory.setAdCount(adCount);

            ViewStats viewStats = viewStatsRepository.findByVideo_IdAndCreatedAt(video.getId(), LocalDate.now())
                    .orElse(new ViewStats(video, 0, 0));
            viewStats.setAdViewCount(viewStats.getAdViewCount() + 1);
            viewStatsRepository.save(viewStats);
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
