package com.ssafy.house.api.community.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.house.api.community.repository.CommunityRepository;
import com.ssafy.house.api.community.dto.internal.CommunityCommentView;
import com.ssafy.house.api.community.dto.internal.CommunityPostPageResult;
import com.ssafy.house.api.community.dto.internal.CommunityPostView;
import com.ssafy.house.api.community.dto.request.CommunityCommentRequest;
import com.ssafy.house.api.community.dto.request.CommunityPostRequest;
import com.ssafy.house.api.community.dto.response.CommunityPostDetailResponse;
import com.ssafy.house.api.community.dto.response.CommunityPostPageResponse;

@ExtendWith(MockitoExtension.class)
class CommunityApplicationServiceTest {

    @Mock
    private CommunityRepository communityRepository;

    @InjectMocks
    private CommunityApplicationService communityApplicationService;

    @Test
    void getPosts_mapsLegacyPageToResponse() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        CommunityPostView post =
                new CommunityPostView(1, "apt", 3, "테스터", "제목", "내용", now, 5, 2, List.of());
        when(communityRepository.getPosts("all", null, 0, 5))
                .thenReturn(new CommunityPostPageResult(List.of(post), 4));

        CommunityPostPageResponse response = communityApplicationService.getPosts("all", null, 0, 5);

        assertThat(response.totalPages()).isEqualTo(4);
        assertThat(response.posts()).hasSize(1);
        assertThat(response.posts().getFirst().title()).isEqualTo("제목");
    }

    @Test
    void getPostDetail_mapsCommentsToResponse() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        CommunityCommentView comment = new CommunityCommentView(10, 1, 7, "댓글러", "댓글 내용", now);
        CommunityPostView post =
                new CommunityPostView(1, "apt", 3, "테스터", "제목", "내용", now, 5, 1, List.of(comment));
        when(communityRepository.getPostDetail(1)).thenReturn(post);

        CommunityPostDetailResponse response = communityApplicationService.getPostDetail(1);

        assertThat(response.comments()).hasSize(1);
        assertThat(response.comments().getFirst().content()).isEqualTo("댓글 내용");
    }

    @Test
    void createPost_mapsRequestToLegacyPost() {
        communityApplicationService.createPost(
                new CommunityPostRequest("apt", "제목", "내용"),
                "tester@example.com");

        verify(communityRepository).createPost("apt", "제목", "내용", "tester@example.com");
    }

    @Test
    void addComment_mapsRequestToLegacyComment() {
        communityApplicationService.addComment(
                1,
                new CommunityCommentRequest("댓글"),
                "tester@example.com");

        verify(communityRepository).addComment(1, "댓글", "tester@example.com");
    }
}

