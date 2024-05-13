package com.reboot.playmoney.controller;

import com.reboot.playmoney.domain.Member;
import com.reboot.playmoney.domain.Video;
import com.reboot.playmoney.dto.*;
import com.reboot.playmoney.service.UserService;
import com.reboot.playmoney.service.VideoService;
import com.reboot.playmoney.service.WatchHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j(topic = "비디오 재생 및 확인")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class VideoApiController {

    private final VideoService videoService;
    private final WatchHistoryService watchHistoryService;
    private final UserService userService;


    // 로그인 시 유저가 동영상 시청
    @PostMapping("/play")
    public ResponseEntity<WatchHistoryResponse> playVideo(Principal principal, @RequestBody PlayRequest playRequest) {
        log.info("비디오 재생 : {}", principal.getName());
        Member member = userService.findByEmail(principal.getName());
        Video video = videoService.findById(playRequest.getVideoId());
        WatchHistoryResponse historyDto = watchHistoryService.playVideo(member, video, playRequest.getPlayTime());
        return ResponseEntity.ok(historyDto);
    }

    // 로그인하지 않고도 동영상 시청 API 요청.
    @PostMapping("/play-video-api")
    public ResponseEntity<WatchHistoryResponse> playVideoNoLogin(@RequestBody PlayRequest playRequest) {
        log.info("비디오 재생 : {}", playRequest.getVideoId());
        Member member = userService.findById(playRequest.getMemberId());
        Video video = videoService.findById(playRequest.getVideoId());
        WatchHistoryResponse historyDto = watchHistoryService.playVideo(member, video, playRequest.getPlayTime());
        return ResponseEntity.ok(historyDto);
    }


    // video 기능 기본 CRUD
    @PostMapping("/videos")
    public ResponseEntity<VideoResponse> addVideo(@RequestBody AddVideoRequest request, Principal principal) {
        log.info("비디오 생성");
        log.info("아이디 확인 : {}", principal.getName());
        Video savedVideo = videoService.save(request, principal);



        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new VideoResponse(savedVideo));
    }

    @GetMapping("/videos")
    public ResponseEntity<List<VideoResponse>> findAllVideos() {
        List<VideoResponse> videos = videoService.findAll()
                .stream()
                .map(VideoResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(videos);
    }
    @GetMapping("/videos/{id}")
    public ResponseEntity<VideoResponse> findVideo(@PathVariable long id) {
        Video video = videoService.findById(id);

        return ResponseEntity.ok()
                .body(new VideoResponse(video));
    }



}

