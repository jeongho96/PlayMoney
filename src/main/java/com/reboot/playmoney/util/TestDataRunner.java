package com.reboot.playmoney.util;

import com.reboot.playmoney.domain.*;
import com.reboot.playmoney.repository.AdvertisementRepository;
import com.reboot.playmoney.repository.UserRepository;
import com.reboot.playmoney.repository.VideoRepository;
import com.reboot.playmoney.repository.VideoViewStatsRepository;
import com.reboot.playmoney.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestDataRunner implements ApplicationRunner {


    private final VideoRepository videoRepository;

    private final VideoViewStatsRepository videoViewStatsRepository;

    private final AdvertisementRepository advertisementRepository;

    private final UserRepository userRepository;


    @Override
    public void run(ApplicationArguments args) {

//        // Check if test data already exists
//        if (userRepository.count() > 0) {
//            // Test data already exists, so we don't need to create test data again
//            return;
//        }
//        // 10명의 유저 생성
//        for (int i = 0; i <= 100; i++) {
//            String username = "User" + i;
////            String password = passwordEncoder.encode("password" + i);
//            String password = "test";
//            String email = "user" + i + "@example.com";
//            Member user = new Member(username, password, email, null , UserType.USER);
//            userRepository.save(user);
//        }
//
//        // 1,2,3번 member_number를 가진 업로더가 올린 각각의 영상 20개
//        for (long i = 1; i <= 5; i++) {
//            long finalI = i;
//            Member user = userRepository.findById(i).orElseThrow(() -> new IllegalArgumentException("User not found: " + finalI));
//            for (int j = 1; j <= 20; j++) {
//                String title = "Video" + j + " by User" + i;
//                Video video = new Video(title, "This is a video by User" + i, LocalDateTime.now(), 0, 1000, user);
//                videoRepository.save(video);
//            }
//        }
//
//        // 각각의 영상에 대한 광고 2개씩 추가
//        List<Video> videos = videoRepository.findAll();
//        for (Video video : videos) {
//            for (int i = 1; i <= 2; i++) {
//                Advertisement ad = new Advertisement(video, LocalDateTime.now(), i);
//                advertisementRepository.save(ad);
//            }
//        }

//        List<Video> videos = videoRepository.findAll();
//        for (Video video : videos) {
//            int j = 350;
//            for( int i = 0; i <= 100; i++){
//                VideoViewStats vs = new VideoViewStats(video, j);
//                j += 10;
//                videoViewStatsRepository.save(vs);
//            }
//        }
    }
}
