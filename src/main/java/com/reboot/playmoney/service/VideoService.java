package com.reboot.playmoney.service;

import com.reboot.playmoney.domain.User;
import com.reboot.playmoney.domain.Video;
import com.reboot.playmoney.dto.AddVideoRequest;
import com.reboot.playmoney.dto.UpdateVideoRequest;
import com.reboot.playmoney.repository.UserRepository;
import com.reboot.playmoney.repository.VideoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    public Video save(AddVideoRequest request, String userName) {
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + userName));

        return videoRepository.save(request.toEntity(user.getId()));
    }

    public List<Video> findAll() {
        return videoRepository.findAll();
    }

    public Video findById(long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
    }

    public void delete(long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        User user = (userRepository.findById(video.getMemberId()))
                .orElseThrow(() -> new IllegalArgumentException("not found user_id : " + video.getMemberId()));

        authorizeArticleAuthor(user);
        videoRepository.delete(video);
    }

    @Transactional
    public void update(long id, UpdateVideoRequest request) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        User user = (userRepository.findById(video.getMemberId()))
                .orElseThrow(() -> new IllegalArgumentException("not found user_id : " + video.getMemberId()));

        authorizeArticleAuthor(user);
        video.update(request.getTitle(), request.getContent(), request.getDuration());

        // 영상의 조회수를 업데이트
        videoRepository.save(video);
    }



    // 게시글을 작성한 유저인지 확인
    private static void authorizeArticleAuthor(User user) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!user.getUsername().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }

}
