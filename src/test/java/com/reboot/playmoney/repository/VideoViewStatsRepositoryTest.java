package com.reboot.playmoney.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.reboot.playmoney.domain.*;
import com.reboot.playmoney.repository.VideoViewStatsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class VideoViewStatsRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VideoViewStatsRepository videoViewStatsRepository;
    @Autowired
    private VideoRepository videoRepository;

    @Test
    public void whenExistingData_thenReturnsTrue() {
        // given
        Member member = Member.builder()
                .email("email@email.com")
                .password("password")
                .username("username")
                .socialProvider(SocialProvider.GOOGLE)
                .userType(UserType.SELLER)
                .build();

        Video video = Video.builder()
                .title("title")
                .content("content")
                .createdAt(LocalDateTime.now())
                .totalViewCount(10)
                .duration(1000)
                .member(member)
                .build();

        entityManager.persistAndFlush(member);
        entityManager.persistAndFlush(video);  // add this line


        LocalDate startDate = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate endDate = LocalDate.now().with(DayOfWeek.SUNDAY);

        VideoViewStats stats = new VideoViewStats(video, startDate, endDate, VideoViewStats.Category.WEEK, 100);
        entityManager.persistAndFlush(stats);

        // when
        boolean exists = videoViewStatsRepository.existsByVideoAndCategoryAndStartDateAndEndDate(video,
                VideoViewStats.Category.WEEK, startDate, endDate);

        // then
        assertThat(exists).isTrue();
    }
}