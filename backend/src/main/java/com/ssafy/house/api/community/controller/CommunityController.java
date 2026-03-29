package com.ssafy.house.api.community.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.api.community.dto.request.CommunityCommentRequest;
import com.ssafy.house.api.community.dto.request.CommunityPostRequest;
import com.ssafy.house.api.community.dto.response.CommunityPostDetailResponse;
import com.ssafy.house.api.community.dto.response.CommunityPostPageResponse;
import com.ssafy.house.api.community.service.CommunityApplicationService;
import com.ssafy.house.global.common.base.BaseResponse;
import com.ssafy.house.global.common.base.BaseResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 신규 커뮤니티 API를 제공하는 컨트롤러이다.
 */
@RestController
@RequestMapping("/v1/community")
@RequiredArgsConstructor
@Tag(name = "Community", description = "커뮤니티 게시글 및 댓글 기능")
public class CommunityController {

    private final CommunityApplicationService communityApplicationService;

    /**
     * 게시글 목록을 조회한다.
     *
     * @param category 카테고리 식별자
     * @param search 검색어
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 게시글 페이지 응답
     */
    @GetMapping("/posts")
    @Operation(summary = "게시글 목록 조회", description = "조건에 맞는 게시글 목록을 페이지 단위로 조회한다.")
    public BaseResponse<CommunityPostPageResponse> getPosts(
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return BaseResponse.onSuccess(communityApplicationService.getPosts(category, search, page, size));
    }

    /**
     * 게시글 상세를 조회한다.
     *
     * @param postId 게시글 식별자
     * @return 게시글 상세 응답
     */
    @GetMapping("/posts/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "게시글 상세와 댓글 목록을 조회한다.")
    public BaseResponse<CommunityPostDetailResponse> getPostDetail(@PathVariable int postId) {
        return BaseResponse.onSuccess(communityApplicationService.getPostDetail(postId));
    }

    /**
     * 게시글을 생성한다.
     *
     * @param request 게시글 요청 데이터
     * @param authentication 인증 정보
     * @return 생성 응답
     */
    @PostMapping("/posts")
    @Operation(summary = "게시글 생성", description = "인증된 사용자가 게시글을 생성한다.")
    public ResponseEntity<BaseResponse<Void>> createPost(
            @RequestBody CommunityPostRequest request,
            Authentication authentication) {
        communityApplicationService.createPost(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.onSuccess(BaseResponseStatus.CREATED, null));
    }

    /**
     * 게시글을 수정한다.
     *
     * @param postId 게시글 식별자
     * @param request 게시글 요청 데이터
     * @param authentication 인증 정보
     * @return 수정 응답
     */
    @PutMapping("/posts/{postId}")
    @Operation(summary = "게시글 수정", description = "인증된 사용자가 자신의 게시글을 수정한다.")
    public ResponseEntity<BaseResponse<Void>> updatePost(
            @PathVariable int postId,
            @RequestBody CommunityPostRequest request,
            Authentication authentication) {
        communityApplicationService.updatePost(postId, request, authentication.getName());
        return ResponseEntity.ok(BaseResponse.onSuccess());
    }

    /**
     * 게시글을 삭제한다.
     *
     * @param postId 게시글 식별자
     * @param authentication 인증 정보
     * @return 삭제 응답
     */
    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "게시글 삭제", description = "인증된 사용자가 자신의 게시글을 삭제한다.")
    public ResponseEntity<BaseResponse<Void>> deletePost(
            @PathVariable int postId,
            Authentication authentication) {
        communityApplicationService.deletePost(postId, authentication.getName());
        return ResponseEntity.ok(BaseResponse.onSuccess());
    }

    /**
     * 댓글을 생성한다.
     *
     * @param postId 게시글 식별자
     * @param request 댓글 요청 데이터
     * @param authentication 인증 정보
     * @return 생성 응답
     */
    @PostMapping("/posts/{postId}/comments")
    @Operation(summary = "댓글 생성", description = "인증된 사용자가 댓글을 생성한다.")
    public ResponseEntity<BaseResponse<Void>> addComment(
            @PathVariable int postId,
            @RequestBody CommunityCommentRequest request,
            Authentication authentication) {
        communityApplicationService.addComment(postId, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.onSuccess(BaseResponseStatus.CREATED, null));
    }

    /**
     * 댓글을 수정한다.
     *
     * @param postId 게시글 식별자
     * @param commentId 댓글 식별자
     * @param request 댓글 요청 데이터
     * @param authentication 인증 정보
     * @return 수정 응답
     */
    @PutMapping("/posts/{postId}/comments/{commentId}")
    @Operation(summary = "댓글 수정", description = "인증된 사용자가 자신의 댓글을 수정한다.")
    public ResponseEntity<BaseResponse<Void>> updateComment(
            @PathVariable int postId,
            @PathVariable int commentId,
            @RequestBody CommunityCommentRequest request,
            Authentication authentication) {
        communityApplicationService.updateComment(postId, commentId, request, authentication.getName());
        return ResponseEntity.ok(BaseResponse.onSuccess());
    }

    /**
     * 댓글을 삭제한다.
     *
     * @param postId 게시글 식별자
     * @param commentId 댓글 식별자
     * @param authentication 인증 정보
     * @return 삭제 응답
     */
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    @Operation(summary = "댓글 삭제", description = "인증된 사용자가 자신의 댓글을 삭제한다.")
    public ResponseEntity<BaseResponse<Void>> deleteComment(
            @PathVariable int postId,
            @PathVariable int commentId,
            Authentication authentication) {
        communityApplicationService.deleteComment(postId, commentId, authentication.getName());
        return ResponseEntity.ok(BaseResponse.onSuccess());
    }
}
