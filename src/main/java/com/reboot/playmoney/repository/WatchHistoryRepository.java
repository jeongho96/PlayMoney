package com.reboot.playmoney.repository;

import com.reboot.playmoney.domain.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
    // memberNumber와 videoNumber를 이용하여 WatchHistory 조회
    Optional<WatchHistory> findByMember_MemberNumberAndVideo_VideoNumber(Long memberNumber, Long videoNumber);
}
