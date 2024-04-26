package com.reboot.playmoney.controller;

import com.reboot.playmoney.domain.User;
import com.reboot.playmoney.domain.Video;
import com.reboot.playmoney.dto.*;
import com.reboot.playmoney.service.UserService;
import com.reboot.playmoney.service.VideoService;
import com.reboot.playmoney.service.WatchHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping("/play")
    public ResponseEntity<WatchHistoryResponse> playVideo(Principal principal, @RequestBody PlayRequest playRequest) {
        log.info("비디오 재생 : {}", principal.getName());
        User user = userService.findByEmail(principal.getName());
        Video video = videoService.findById(playRequest.getVideoId());
        WatchHistoryResponse historyDto = watchHistoryService.playVideo(user, video, playRequest.getPlayTime());
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
//
//    @DeleteMapping("/video/{id}")
//    public ResponseEntity<Void> deleteVideo(@PathVariable long id) {
//        videoService.delete(id);
//
//        return ResponseEntity.ok()
//                .build();
//    }
//
//    @PutMapping("/video/{id}")
//    public ResponseEntity<Video> updateVideo(@PathVariable long id,
//                                             @RequestBody UpdateVideoRequest request) {
//        videoService.update(id, request);
//
//        Video updatedVideo = videoService.findById(id);
//
//        return ResponseEntity.ok()
//                .body(updatedVideo);
//    }

}

