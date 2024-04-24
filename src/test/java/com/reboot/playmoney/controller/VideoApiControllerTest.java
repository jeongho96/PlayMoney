package com.reboot.playmoney.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reboot.playmoney.domain.Video;
import com.reboot.playmoney.dto.AddVideoRequest;
import com.reboot.playmoney.dto.UpdateVideoRequest;
import com.reboot.playmoney.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VideoApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper; // 직렬화, 역직렬화에 쓰이는 매퍼
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    VideoRepository videoRepository;

    @BeforeEach
    public void mockMvcSetUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        videoRepository.deleteAll();
    }

//    @DisplayName("addVideo : 비디오 추가에 성공")
//    @Test
//    void addVideo() throws Exception {
//        // given
//        final String url = "/api/articles";
//        final String title = "title";
//        final String content = "content";
//        final AddVideoRequest userRequest = new AddVideoRequest(title, content);
//
//        // 객체 JSON으로 직렬화(객체 -> JSON)
//        final String requestBody = objectMapper.writeValueAsString(userRequest);
//
//        // when
//        // 설정 내용 기반 요청 전송
//        // post 임포트 조심.
//        ResultActions result = mockMvc.perform(post(url)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(requestBody));
//
//        // then
//        result.andExpect(status().isCreated());
//
//        List<Video> videos = videoRepository.findAll();
//
//        assertThat(videos.size()).isEqualTo(1); // 크기가 1인가?
//        assertThat(videos.get(0).getTitle()).isEqualTo(title); // 제목이 title?
//        assertThat(videos.get(0).getContent()).isEqualTo(content);
//    }


    @DisplayName("findAllArticles: 아티클 목록 조회에 성공한다.")
    @Test
    public void findAllArticles() throws Exception {
        // given
        final String url = "/api/articles";
        Video savedVideo = createDefaultArticle();

        // when
        final ResultActions resultActions = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(savedVideo.getContent()))
                .andExpect(jsonPath("$[0].title").value(savedVideo.getTitle()));
    }

    @DisplayName("findArticle: 아티클 단건 조회에 성공한다.")
    @Test
    public void findArticle() throws Exception {
        // given
        final String url = "/api/articles/{id}";
        Video savedVideo = createDefaultArticle();

        // when
        final ResultActions resultActions = mockMvc.perform(get(url, savedVideo.getId()));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(savedVideo.getContent()))
                .andExpect(jsonPath("$.title").value(savedVideo.getTitle()));
    }


    @DisplayName("deleteArticle: 아티클 삭제에 성공한다.")
    @Test
    public void deleteArticle() throws Exception {
        // given
        final String url = "/api/articles/{id}";
        Video savedVideo = createDefaultArticle();

        // when
        mockMvc.perform(delete(url, savedVideo.getId()))
                .andExpect(status().isOk());

        // then
        List<Video> videos = videoRepository.findAll();

        assertThat(videos).isEmpty();
    }


    @DisplayName("updateVideo: 아티클 수정에 성공한다.")
    @Test
    public void updateVideo() throws Exception {
        // given
        final String url = "/api/articles/{id}";
        Video savedVideo = createDefaultArticle();

        final String newTitle = "new title";
        final String newContent = "new content";

        UpdateVideoRequest request = new UpdateVideoRequest(newTitle, newContent);

        // when
        ResultActions result = mockMvc.perform(put(url, savedVideo.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk());

        Video video = videoRepository.findById(savedVideo.getId()).get();

        assertThat(video.getTitle()).isEqualTo(newTitle);
        assertThat(video.getContent()).isEqualTo(newContent);
    }

    private Video createDefaultArticle() {
        return videoRepository.save(Video.builder()
                .title("title")
//                .author(user.getUsername())
                .content("content")
                .build());
    }
}