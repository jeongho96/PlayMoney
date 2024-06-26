package com.reboot.playmoney.service;

import com.reboot.playmoney.domain.Member;
import com.reboot.playmoney.domain.Video;
import com.reboot.playmoney.dto.AddVideoRequest;
import com.reboot.playmoney.repository.UserRepository;
import com.reboot.playmoney.repository.VideoRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    public Video save(AddVideoRequest request, Principal principal) {
        Member member = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return videoRepository.save(request.toEntity(member));
    }

    public List<Video> findAll() {
        return videoRepository.findAll();
    }

    public Video findById(long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
    }


//
//    @Transactional
//    public void delete(long id) {
//        Video video = videoRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
//
//        User user = (userRepository.findById(video.getMemberId()))
//                .orElseThrow(() -> new IllegalArgumentException("not found user_id : " + video.getMemberId()));
//
//        authorizeArticleAuthor(user);
//        videoRepository.delete(video);
//    }
//
//    @Transactional
//    public void update(long id, UpdateVideoRequest request) {
//        Video video = videoRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
//
//        User user = (userRepository.findById(video.getMemberId()))
//                .orElseThrow(() -> new IllegalArgumentException("not found user_id : " + video.getMemberId()));
//
//        authorizeArticleAuthor(user);
//        video.update(request.getTitle(), request.getContent(), request.getDuration());
//
//        // 영상의 조회수를 업데이트
//        videoRepository.save(video);
//    }
//    // 게시글을 작성한 유저인지 확인
//    private static void authorizeArticleAuthor(Member member) {
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        if (!member.getUsername().equals(userName)) {
//            throw new IllegalArgumentException("not authorized");
//        }
//    }


}
