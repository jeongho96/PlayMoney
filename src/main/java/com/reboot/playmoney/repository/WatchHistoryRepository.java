package com.reboot.playmoney.repository;

import com.reboot.playmoney.domain.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
    // memberId와 videoId를 이용하여 WatchHistory 조회
    Optional<WatchHistory> findByMemberIdAndVideoId(Long memberId, Long videoId);
}
