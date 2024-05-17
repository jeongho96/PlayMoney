//package com.reboot.playmoney.batch;
//
//import com.reboot.playmoney.domain.AdViewStats;
//import com.reboot.playmoney.domain.VideoViewStats;
//import jakarta.persistence.EntityManagerFactory;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.NonTransientResourceException;
//import org.springframework.batch.item.ParseException;
//import org.springframework.batch.item.UnexpectedInputException;
//import org.springframework.batch.item.database.JpaPagingItemReader;
//
//import java.time.LocalDate;
//import java.util.Collections;
//import java.util.Map;
//
//public class SalesCustomItemReader implements ItemReader<VideoViewStats> {
//
//    private final JpaPagingItemReader<VideoViewStats> videoViewStatsReader;
//
//    // 생성자에서 Reader 초기화
//    public SalesCustomItemReader(EntityManagerFactory entityManagerFactory) {
//        videoViewStatsReader = new JpaPagingItemReader<>();
//        videoViewStatsReader.setEntityManagerFactory(entityManagerFactory);
//        videoViewStatsReader.setQueryString("SELECT v FROM VideoViewStats v WHERE v.category = 'DAY' AND v.startDate = :yesterday");
//
//        Map<String, Object> params = Collections.singletonMap("yesterday", LocalDate.now().minusDays(1));
//        videoViewStatsReader.setParameterValues(params);
//    }
//
//    @Override
//    public VideoViewStats read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
//        VideoViewStats videoStats = videoViewStatsReader.read();
//
//        if (videoStats == null) {
//            return null;
//        }
//
//        // 여기에서 videoStats와 adStats를 조합하여 CustomItem 객체를 생성하고 반환
//        return videoStats;
//    }
//}
