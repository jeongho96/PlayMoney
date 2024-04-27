package com.reboot.playmoney.service;

import com.reboot.playmoney.domain.Member;
import com.reboot.playmoney.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public Member loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                 .orElseThrow(() -> new IllegalArgumentException((email)));
    }
}
