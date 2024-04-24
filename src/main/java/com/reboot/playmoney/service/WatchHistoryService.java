package com.reboot.playmoney.service;

import com.reboot.playmoney.domain.User;
import com.reboot.playmoney.domain.WatchHistory;
import com.reboot.playmoney.dto.WatchHistoryRequest;
import com.reboot.playmoney.repository.UserRepository;
import com.reboot.playmoney.repository.WatchHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class WatchHistoryService {

    private final WatchHistoryRepository watchHistoryRepository;
    private final UserRepository userRepository;

    // 시청 기록 저장
    public WatchHistory save(WatchHistoryRequest request, String userName){
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + userName));

        return watchHistoryRepository.save(request.toEntity(user.getId()));
    }

    public List<WatchHistory> findAll(){
        return watchHistoryRepository.findAll();
    }

    public WatchHistory getWatchHistory(Long memberId, Long videoId) {
        return watchHistoryRepository.findByMemberIdAndVideoId(memberId, videoId)
                .orElse(null); // 해당 memberId와 videoId로 기록을 찾고, 없으면 null 반환
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
