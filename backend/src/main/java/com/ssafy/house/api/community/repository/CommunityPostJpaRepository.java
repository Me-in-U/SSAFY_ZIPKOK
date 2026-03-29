package com.ssafy.house.api.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.house.api.community.entity.CommunityPostEntity;

/**
 * 커뮤니티 게시글 엔티티의 기본 CRUD를 제공하는 JPA repository이다.
 */
public interface CommunityPostJpaRepository extends JpaRepository<CommunityPostEntity, Integer> {
}
