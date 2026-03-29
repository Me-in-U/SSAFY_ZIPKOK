package com.ssafy.house.api.community.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.house.api.community.repository.CommunityRepository;
import com.ssafy.house.api.community.dto.internal.CommunityCommentView;
import com.ssafy.house.api.community.dto.internal.CommunityPostPageResult;
import com.ssafy.house.api.community.dto.internal.CommunityPostView;
import com.ssafy.house.api.community.dto.request.CommunityCommentRequest;
import com.ssafy.house.api.community.dto.request.CommunityPostRequest;
import com.ssafy.house.api.community.dto.response.CommunityCommentResponse;
import com.ssafy.house.api.community.dto.response.CommunityPostDetailResponse;
import com.ssafy.house.api.community.dto.response.CommunityPostPageResponse;
import com.ssafy.house.api.community.dto.response.CommunityPostSummaryResponse;

import lombok.RequiredArgsConstructor;

/**
 * 커뮤니티 도메인을 신규 API 구조에 맞게 중계하는 서비스이다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommunityApplicationService {

    private final CommunityRepository communityRepository;

    /**
     * 게시글 목록을 조회한다.
     *
     * @param categoryId 카테고리 식별자
     * @param searchQuery 검색어
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 게시글 페이지 응답
     */
    public CommunityPostPageResponse getPosts(String categoryId, String searchQuery, int page, int size) {
        CommunityPostPageResult pageResult = communityRepository.getPosts(categoryId, searchQuery, page, size);
        return new CommunityPostPageResponse(pageResult.posts().stream()
                .map(this::mapToPostSummaryResponse)
                .toList(), pageResult.totalPages());
    }

    /**
     * 게시글 상세 정보를 조회한다.
     *
     * @param postId 게시글 식별자
     * @return 게시글 상세 응답
     */
    public CommunityPostDetailResponse getPostDetail(int postId) {
        return mapToPostDetailResponse(communityRepository.getPostDetail(postId));
    }

    /**
     * 게시글을 생성한다.
     *
     * @param request 게시글 요청 데이터
     * @param userEmail 인증된 사용자 이메일
     */
    @Transactional
    public void createPost(CommunityPostRequest request, String userEmail) {
        communityRepository.createPost(request.categoryId(), request.title(), request.content(), userEmail);
    }

    /**
     * 게시글을 수정한다.
     *
     * @param postId 게시글 식별자
     * @param request 게시글 요청 데이터
     * @param userEmail 인증된 사용자 이메일
     */
    @Transactional
    public void updatePost(int postId, CommunityPostRequest request, String userEmail) {
        communityRepository.updatePost(postId, request.categoryId(), request.title(), request.content(), userEmail);
    }

    /**
     * 게시글을 삭제한다.
     *
     * @param postId 게시글 식별자
     * @param userEmail 인증된 사용자 이메일
     */
    @Transactional
    public void deletePost(int postId, String userEmail) {
        communityRepository.deletePost(postId, userEmail);
    }

    /**
     * 댓글을 생성한다.
     *
     * @param postId 게시글 식별자
     * @param request 댓글 요청 데이터
     * @param userEmail 인증된 사용자 이메일
     */
    @Transactional
    public void addComment(int postId, CommunityCommentRequest request, String userEmail) {
        communityRepository.addComment(postId, request.content(), userEmail);
    }

    /**
     * 댓글을 수정한다.
     *
     * @param postId 게시글 식별자
     * @param commentId 댓글 식별자
     * @param request 댓글 요청 데이터
     * @param userEmail 인증된 사용자 이메일
     */
    @Transactional
    public void updateComment(int postId, int commentId, CommunityCommentRequest request, String userEmail) {
        communityRepository.updateComment(postId, commentId, request.content(), userEmail);
    }

    /**
     * 댓글을 삭제한다.
     *
     * @param postId 게시글 식별자
     * @param commentId 댓글 식별자
     * @param userEmail 인증된 사용자 이메일
     */
    @Transactional
    public void deleteComment(int postId, int commentId, String userEmail) {
        communityRepository.deleteComment(postId, commentId, userEmail);
    }

    private CommunityPostSummaryResponse mapToPostSummaryResponse(CommunityPostView post) {
        return new CommunityPostSummaryResponse(
                post.postId(),
                post.categoryId(),
                post.authorId(),
                post.authorName(),
                post.title(),
                post.content(),
                post.createdAt(),
                post.views(),
                post.commentCount());
    }

    private CommunityPostDetailResponse mapToPostDetailResponse(CommunityPostView post) {
        List<CommunityCommentResponse> comments = post.comments().stream().map(this::mapToCommentResponse).toList();
        return new CommunityPostDetailResponse(
                post.postId(),
                post.categoryId(),
                post.authorId(),
                post.authorName(),
                post.title(),
                post.content(),
                post.createdAt(),
                post.views(),
                post.commentCount(),
                comments);
    }

    private CommunityCommentResponse mapToCommentResponse(CommunityCommentView comment) {
        return new CommunityCommentResponse(
                comment.commentId(),
                comment.postId(),
                comment.authorId(),
                comment.authorName(),
                comment.content(),
                comment.createdAt());
    }
}

