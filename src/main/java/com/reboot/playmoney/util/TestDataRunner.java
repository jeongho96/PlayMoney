package com.reboot.playmoney.util;

import com.reboot.playmoney.domain.*;
import com.reboot.playmoney.repository.*;
import com.reboot.playmoney.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//@Component
@RequiredArgsConstructor
public class TestDataRunner implements ApplicationRunner {


    private final VideoRepository videoRepository;

    private final VideoViewStatsRepository videoViewStatsRepository;

    private final AdvertisementRepository advertisementRepository;

    private final UserRepository userRepository;

    private final SalesRepository salesRepository;


    @Override
    public void run(ApplicationArguments args) {



//        for (Long memberId = 1L; memberId <= 100; memberId++) {
//            Long finalMemberId = memberId;
//            Member member = userRepository.findById(memberId)
//                    .orElseThrow(() -> new IllegalArgumentException("Member not found: " + finalMemberId)); // Retrieve the member
//
//            List<Video> videos = new ArrayList<>();
//            for (int i = 1; i <= 1000; i++) {
//                Video video = Video.builder()
//                        .member(member)
//                        .title("Video" + i + "by" + "User" + memberId)
//                        .content("This is a video by User" + memberId)
//                        .totalViewCount(0)
//                        .duration(1500)
//                        .build();
//
//                videos.add(video);
//            }
//
//            videoRepository.saveAll(videos);  // Save all videos at once
//        }


        // 1 ~ 100000번 비디오에 대해서 (20번 단위로 멤버 번호는 1 ~ 5까지 상승한다.)
        // 각각 4월 1일부터 30일까지의 정산 금액을 넣는다.
/*
        float videoSale = 100;
        float adSale = 150;
        LocalDate currentMonth = LocalDate.now().minusMonths(1);



        for(Long i = 1L; i <= 100000L; i++){

            Long memberId = (i - 1) / 1000 + 1;
            Member member = userRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));
            List<Sales> salesList = new ArrayList<>();  // Create a list to hold Sales objects
            for(int j = 1; j <=30; j++){
                Long finalI = i;
                Video video = videoRepository.findById(i).orElseThrow(() -> new IllegalArgumentException("Video not found: " + finalI));
                LocalDate startDate = currentMonth.withDayOfMonth(j);
                LocalDate endDate = currentMonth.withDayOfMonth(j);
                Sales sales = Sales.builder()
                        .member(member)
                        .video(video)
                        .startDate(startDate)
                        .endDate(endDate)
                        .category(DayCategory.DAY)
                        .videoSaleAmount(videoSale + (j * 150))
                        .adSaleAmount(adSale + (j * 200))
                        .build();

                salesList.add(sales);    // Add the sales object to the list
            }
            salesRepository.saveAll(salesList);  // Save all sales objects at once
        }
*/



//        // Check if test data already exists
//        if (userRepository.count() > 0) {
//            // Test data already exists, so we don't need to create test data again
//            return;
//        }
//        // 100명의 유저 생성
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
//        for (long i = 1; i <= 100; i++) {
//            long finalI = i;
//            Member user = userRepository.findById(i).orElseThrow(() -> new IllegalArgumentException("User not found: " + finalI));
//            for (int j = 1; j <= 100; j++) {
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
