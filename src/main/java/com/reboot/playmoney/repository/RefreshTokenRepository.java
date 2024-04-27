package com.reboot.playmoney.repository;


import com.reboot.playmoney.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByMemberNumber(Long memberNumber);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}

