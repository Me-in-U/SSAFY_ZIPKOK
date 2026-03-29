package com.ssafy.house.api.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.house.api.member.entity.MemberEntity;

/**
 * 회원 엔티티의 기본 CRUD를 제공하는 JPA repository이다.
 */
public interface MemberJpaRepository extends JpaRepository<MemberEntity, Integer> {

    /**
     * 이메일로 회원을 조회한다.
     *
     * @param email 회원 이메일
     * @return 회원 엔티티
     */
    Optional<MemberEntity> findByEmail(String email);
}
