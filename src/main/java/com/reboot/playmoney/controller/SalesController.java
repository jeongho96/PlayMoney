package com.reboot.playmoney.controller;


import com.reboot.playmoney.domain.Sales;
import com.reboot.playmoney.dto.SalesViewResponse;
import com.reboot.playmoney.service.SalesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    // 유저 별 정산 금액 조회 기능.(일단 일괄로 조회)
    @GetMapping("/sales/{id}")
    public ResponseEntity<List<SalesViewResponse>> getSales(@PathVariable Long id) {
        List<SalesViewResponse> salesList = salesService.getSalesList(id);
        return ResponseEntity.ok(salesList);
    }
}
