package com.reboot.playmoney.service;

import com.reboot.playmoney.config.jwt.TokenProvider;
import com.reboot.playmoney.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken) {
        // 토큰 유효성 검사에 실패하면 예외 발생
        if(!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getMemberNumber();
        Member member = userService.findById(userId);

        return tokenProvider.generateToken(member, Duration.ofHours(2));
    }
}

