package com.ssafy.house.api.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.house.api.member.entity.MemberFavoriteEntity;

/**
 * 회원 즐겨찾기 엔티티의 기본 CRUD를 제공하는 JPA repository이다.
 */
public interface MemberFavoriteJpaRepository extends JpaRepository<MemberFavoriteEntity, Long> {

    /**
     * 회원 번호와 아파트 식별자로 즐겨찾기 존재 여부를 확인한다.
     *
     * @param mno 회원 번호
     * @param aptSeq 아파트 식별자
     * @return 존재 여부
     */
    boolean existsByMnoAndAptSeq(Integer mno, String aptSeq);

    /**
     * 회원 번호와 아파트 식별자로 즐겨찾기를 삭제한다.
     *
     * @param mno 회원 번호
     * @param aptSeq 아파트 식별자
     */
    void deleteByMnoAndAptSeq(Integer mno, String aptSeq);
}
