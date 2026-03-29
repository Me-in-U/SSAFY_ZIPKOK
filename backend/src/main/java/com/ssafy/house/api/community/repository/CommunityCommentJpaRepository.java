package com.ssafy.house.api.community.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.house.api.community.entity.CommunityCommentEntity;

/**
 * 커뮤니티 댓글 엔티티의 기본 CRUD를 제공하는 JPA repository이다.
 */
public interface CommunityCommentJpaRepository extends JpaRepository<CommunityCommentEntity, Integer> {

    /**
     * 게시글 식별자로 댓글 목록을 작성일 순으로 조회한다.
     *
     * @param postId 게시글 식별자
     * @return 댓글 목록
     */
    List<CommunityCommentEntity> findAllByPostIdOrderByCreatedAtAsc(Integer postId);
}
