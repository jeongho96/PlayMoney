package com.reboot.playmoney.controller;

import com.reboot.playmoney.domain.User;
import com.reboot.playmoney.domain.Video;
import com.reboot.playmoney.domain.WatchHistory;
import com.reboot.playmoney.dto.*;
import com.reboot.playmoney.service.UserService;
import com.reboot.playmoney.service.VideoService;
import com.reboot.playmoney.service.WatchHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class VideoApiController {

    private final VideoService videoService;
    private final WatchHistoryService watchHistoryService;
    private final UserService userService;

    // 비디오 재생 기능 추가.
    @PostMapping("/api/videos/play-video")
    public ResponseEntity<WatchHistory> addHistory(@Valid @RequestBody WatchHistoryRequest request, Principal principal){
        User user = userService.findByEmail(principal.getName());
        Video video = videoService.findById(request.getVideoId());

        // memberId와 videoId로 기존의 시청 기록 조회
        WatchHistory watchHistory = watchHistoryService.getWatchHistory(user.getId(), request.getVideoId());

        // 최종 시청 위치는 기존 시청 시간 + 지금 보내는 시간
        int playTime = request.getPlayTime();

        // 시청 기록이 없으면 생성.
        if (watchHistory == null) {

            // 시청 시간이 영상 길이를 넘어가면 최종 시청 위치를 0으로 초기화하고 조회수 1 증가.
            if(playTime > video.getDuration()){
                video.setViewCount(video.getViewCount() + 1);
                playTime = 0;
                request.setPlayTime(playTime);
            }

            watchHistory = watchHistoryService.save(request, principal.getName());

        }
        // 시청 기록이 존재하면 시청 시간을 누적해 최근 시청 시간 변경.
        else {
            int totalPlayTime = watchHistory.getPlayTime() + playTime;
            // 시청 시간이 영상 길이를 넘어가면 최종 시청 위치를 0으로 초기화하고 조회수 1 증가.
            if (totalPlayTime > video.getDuration()) {
                video.setViewCount(video.getViewCount() + 1);
                totalPlayTime = 0;
            }
            // 기록이 있으면 최종 시청 위치와 조회수 업데이트
            watchHistoryService.updatePlayTime(watchHistory.getId(), totalPlayTime);


        }

        // 영상의 조회수 업데이트
        videoService.update(video.getId(), new UpdateVideoRequest(video.getTitle(), video.getContent(), video.getDuration(), video.getViewCount()));

        return ResponseEntity.ok()
                .body(watchHistory);
    }







    // video 기능 기본 CRUD
    @PostMapping("/api/videos")
    public ResponseEntity<Video> addVideo(@RequestBody AddVideoRequest request, Principal principal) {
        Video savedVideo = videoService.save(request, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedVideo);
    }

    @GetMapping("/api/videos")
    public ResponseEntity<List<VideoResponse>> findAllVideos() {
        List<VideoResponse> videos = videoService.findAll()
                .stream()
                .map(VideoResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(videos);
    }
    @GetMapping("/api/videos/{id}")
    public ResponseEntity<VideoResponse> findVideo(@PathVariable long id) {
        Video video = videoService.findById(id);

        return ResponseEntity.ok()
                .body(new VideoResponse(video));
    }

    @DeleteMapping("/api/videos/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable long id) {
        videoService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/videos/{id}")
    public ResponseEntity<Video> updateVideo(@PathVariable long id,
                                             @RequestBody UpdateVideoRequest request) {
        videoService.update(id, request);

        Video updatedVideo = videoService.findById(id);

        return ResponseEntity.ok()
                .body(updatedVideo);
    }

}

