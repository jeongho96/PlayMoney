package com.reboot.playmoney.controller;

import com.reboot.playmoney.domain.User;
import com.reboot.playmoney.domain.Video;
import com.reboot.playmoney.dto.VideoListViewResponse;
import com.reboot.playmoney.dto.VideoViewResponse;
import com.reboot.playmoney.service.UserService;
import com.reboot.playmoney.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class VideoViewController {

    private final VideoService videoService;
    private final UserService userService;

    @GetMapping("/videos")
    public String getVideos(Model model) {
        List<VideoListViewResponse> videos = videoService.findAll()
                .stream()
                .map(VideoListViewResponse::new)
                .toList();

        model.addAttribute("videos", videos);

        return "videoList";
    }

    @GetMapping("/videos/{id}")
    public String getVideo(@PathVariable Long id, Model model) {
        Video video = videoService.findById(id);
        // 비디오 업로더의 정보 가져오기.
        User user = video.getUser();

        System.out.println("user 이메일 : " + user.getName());

        model.addAttribute("video", new VideoViewResponse(video));
        model.addAttribute("userName", user.getName());

        return "videoPage";
    }


    @GetMapping("/new-video")
    public String newVideo(@RequestParam(required = false) Long id, Model model) {
        if (id == null) {
            model.addAttribute("video", new VideoViewResponse());
        } else {
            Video video = videoService.findById(id);
            model.addAttribute("video", new VideoViewResponse(video));
        }

        return "newVideo";
    }
}
