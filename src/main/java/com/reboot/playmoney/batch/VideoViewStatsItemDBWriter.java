package com.reboot.playmoney.batch;

import com.reboot.playmoney.domain.VideoViewStats;
import com.reboot.playmoney.repository.VideoViewStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VideoViewStatsItemDBWriter implements ItemWriter<VideoViewStats> {

    private final VideoViewStatsRepository videoViewStatsRepository;

    @Override
    public void write(Chunk<? extends VideoViewStats> chunk) throws Exception {
        for(VideoViewStats stat : chunk){
            videoViewStatsRepository.save(stat);
        }
    }
}