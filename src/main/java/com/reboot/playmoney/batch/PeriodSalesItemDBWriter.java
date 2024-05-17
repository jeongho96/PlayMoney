package com.reboot.playmoney.batch;

import com.reboot.playmoney.domain.Sales;
import com.reboot.playmoney.repository.SalesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PeriodSalesItemDBWriter implements ItemWriter<Sales> {

    private final SalesRepository salesRepository;

    @Override
    public void write(Chunk<? extends Sales> chunk) throws Exception {
        for(Sales sales : chunk){
            log.info("Writing Ad sales {}",sales.getAdSaleAmount());
            log.info("Writing Video sales {}",sales.getVideoSaleAmount());
            salesRepository.save(sales);
        }
    }
}
