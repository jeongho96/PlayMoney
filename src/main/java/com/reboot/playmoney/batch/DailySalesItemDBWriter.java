package com.reboot.playmoney.batch;

import com.reboot.playmoney.domain.Sales;
import com.reboot.playmoney.domain.VideoViewStats;
import com.reboot.playmoney.repository.SalesRepository;
import com.reboot.playmoney.repository.VideoViewStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailySalesItemDBWriter implements ItemWriter<Sales> {

    private final SalesRepository salesRepository;

    @Override
    public void write(Chunk<? extends Sales> chunk) throws Exception {
        for(Sales sales : chunk){
            salesRepository.save(sales);
        }
    }
}
