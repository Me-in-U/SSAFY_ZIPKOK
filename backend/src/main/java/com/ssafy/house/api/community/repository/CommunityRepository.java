package com.ssafy.house.api.community.repository;

import java.sql.Timestamp;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ssafy.house.api.community.entity.CommunityCommentEntity;
import com.ssafy.house.api.community.entity.CommunityPostEntity;
import com.ssafy.house.api.community.dto.internal.CommunityPostPageResult;
import com.ssafy.house.api.community.dto.internal.CommunityPostView;
import com.ssafy.house.api.member.entity.MemberEntity;
import com.ssafy.house.api.member.repository.MemberJpaRepository;

import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;

/**
 * 커뮤니티 도메인의 JPA 및 query repository를 조합하는 facade repository이다.
 */
@Repository
@RequiredArgsConstructor
public class CommunityRepository {

    private final CommunityPostJpaRepository communityPostJpaRepository;
    private final CommunityCommentJpaRepository communityCommentJpaRepository;
    private final CommunityPostQueryRepository communityPostQueryRepository;
    private final CommunityCommentQueryRepository communityCommentQueryRepository;
    private final MemberJpaRepository memberJpaRepository;

    /**
     * 게시글 목록을 조회한다.
     *
     * @param categoryId 카테고리 식별자
     * @param searchQuery 검색어
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 게시글 페이지 결과
     */
    public CommunityPostPageResult getPosts(String categoryId, String searchQuery, int page, int size) {
        return communityPostQueryRepository.findPostPage(categoryId, searchQuery, page, size);
    }

    /**
     * 게시글 상세를 조회한다.
     *
     * @param postId 게시글 식별자
     * @return 게시글 상세
     */
    @Transactional
    public CommunityPostView getPostDetail(int postId) {
        CommunityPostEntity post = communityPostJpaRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        post.setViews(post.getViews() == null ? 1 : post.getViews() + 1);
        communityPostJpaRepository.save(post);
        return communityPostQueryRepository.findPostDetail(
                postId,
                communityCommentQueryRepository.findCommentsByPostId(postId));
    }

    /**
     * 게시글을 생성한다.
     *
     * @param categoryId 카테고리 식별자
     * @param title 제목
     * @param content 내용
     * @param userEmail 작성자 이메일
     */
    @Transactional
    public void createPost(String categoryId, String title, String content, String userEmail) {
        MemberEntity member = findMember(userEmail);
        communityPostJpaRepository.save(CommunityPostEntity.builder()
                .categoryId(categoryId)
                .authorId(member.getMno())
                .title(title)
                .content(content)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .views(0)
                .build());
    }

    /**
     * 게시글을 수정한다.
     *
     * @param postId 게시글 식별자
     * @param categoryId 카테고리 식별자
     * @param title 제목
     * @param content 내용
     * @param userEmail 작성자 이메일
     */
    @Transactional
    public void updatePost(int postId, String categoryId, String title, String content, String userEmail) {
        MemberEntity member = findMember(userEmail);
        CommunityPostEntity post = communityPostJpaRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 없습니다."));
        validateAuthor(post.getAuthorId(), member.getMno(), "게시글 수정 권한이 없습니다.");
        post.setCategoryId(categoryId);
        post.setTitle(title);
        post.setContent(content);
        communityPostJpaRepository.save(post);
    }

    /**
     * 게시글을 삭제한다.
     *
     * @param postId 게시글 식별자
     * @param userEmail 작성자 이메일
     */
    @Transactional
    public void deletePost(int postId, String userEmail) {
        MemberEntity member = findMember(userEmail);
        CommunityPostEntity post = communityPostJpaRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 없습니다."));
        validateAuthor(post.getAuthorId(), member.getMno(), "게시글 삭제 권한이 없습니다.");
        communityPostJpaRepository.delete(post);
    }

    /**
     * 댓글을 생성한다.
     *
     * @param postId 게시글 식별자
     * @param content 댓글 내용
     * @param userEmail 작성자 이메일
     */
    @Transactional
    public void addComment(int postId, String content, String userEmail) {
        MemberEntity member = findMember(userEmail);
        communityCommentJpaRepository.save(CommunityCommentEntity.builder()
                .postId(postId)
                .authorId(member.getMno())
                .content(content)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build());
    }

    /**
     * 댓글을 수정한다.
     *
     * @param postId 게시글 식별자
     * @param commentId 댓글 식별자
     * @param content 댓글 내용
     * @param userEmail 작성자 이메일
     */
    @Transactional
    public void updateComment(int postId, int commentId, String content, String userEmail) {
        MemberEntity member = findMember(userEmail);
        CommunityCommentEntity comment = communityCommentJpaRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 없습니다."));
        validateAuthor(comment.getAuthorId(), member.getMno(), "댓글 수정 권한이 없습니다.");
        comment.setContent(content);
        communityCommentJpaRepository.save(comment);
    }

    /**
     * 댓글을 삭제한다.
     *
     * @param postId 게시글 식별자
     * @param commentId 댓글 식별자
     * @param userEmail 작성자 이메일
     */
    @Transactional
    public void deleteComment(int postId, int commentId, String userEmail) {
        MemberEntity member = findMember(userEmail);
        CommunityCommentEntity comment = communityCommentJpaRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 없습니다."));
        validateAuthor(comment.getAuthorId(), member.getMno(), "댓글 삭제 권한이 없습니다.");
        communityCommentJpaRepository.delete(comment);
    }

    private MemberEntity findMember(String userEmail) {
        return memberJpaRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인된 사용자 정보가 없습니다."));
    }

    private void validateAuthor(Integer authorId, Integer currentAuthorId, String message) {
        if (!authorId.equals(currentAuthorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
        }
    }
}


