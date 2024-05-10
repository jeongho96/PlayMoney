package com.reboot.playmoney.service;


import com.reboot.playmoney.domain.Member;
import com.reboot.playmoney.domain.Sales;
import com.reboot.playmoney.dto.SalesViewResponse;
import com.reboot.playmoney.repository.SalesRepository;
import com.reboot.playmoney.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SalesService {

    private final SalesRepository salesRepository;
    private final UserRepository userRepository;

    public List<SalesViewResponse> getSalesList(Long id) {
        Member member = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
        List<Sales> salesList = salesRepository.findByMember(member);
        List<SalesViewResponse> salesResponseList = new ArrayList<>();

        for(Sales sales : salesList){
            salesResponseList.add(SalesViewResponse.builder()
                            .videoNumber(sales.getVideo().getVideoNumber())
                            .videoTitle(sales.getVideo().getTitle())
                            .videoSaleAmount(sales.getVideoSaleAmount())
                            .adSaleAmount(sales.getAdSaleAmount())
                            .category(sales.getCategory())
                            .startDate(sales.getStartDate())
                            .endDate(sales.getEndDate())
                    .build());
        }

        return salesResponseList;
    }
}
