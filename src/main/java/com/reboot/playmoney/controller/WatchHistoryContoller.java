package com.reboot.playmoney.controller;


import com.reboot.playmoney.domain.WatchHistory;
import com.reboot.playmoney.dto.WatchHistoryRequest;
import com.reboot.playmoney.dto.WatchHistoryResponse;
import com.reboot.playmoney.service.WatchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class WatchHistoryContoller {

    private final WatchHistoryService watchHistoryService;


    @GetMapping("/api/history")
    public ResponseEntity<List<WatchHistoryResponse>> getWatchHistory() {
        List<WatchHistoryResponse> historys = watchHistoryService.findAll()
                .stream().map(WatchHistoryResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(historys);
    }



}
