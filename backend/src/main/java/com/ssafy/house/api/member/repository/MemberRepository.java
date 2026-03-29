package com.ssafy.house.api.member.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ssafy.house.api.member.entity.MemberEntity;
import com.ssafy.house.api.member.entity.MemberFavoriteEntity;
import com.ssafy.house.api.member.dto.internal.MemberAccount;
import com.ssafy.house.api.member.dto.internal.MemberFavoriteSummary;
import com.ssafy.house.api.member.dto.internal.MemberPageResult;

import lombok.RequiredArgsConstructor;

/**
 * 회원 도메인의 JPA 및 query repository를 조합하는 facade repository이다.
 */
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberFavoriteJpaRepository memberFavoriteJpaRepository;
    private final MemberSearchQueryRepository memberSearchQueryRepository;
    private final MemberFavoriteQueryRepository memberFavoriteQueryRepository;

    /**
     * 이메일로 회원을 조회한다.
     *
     * @param email 회원 이메일
     * @return 회원 정보
     */
    public MemberAccount findByEmail(String email) {
        return memberJpaRepository.findByEmail(email)
                .map(this::mapToAccount)
                .orElse(null);
    }

    /**
     * 회원을 저장한다.
     *
     * @param member 저장할 회원
     */
    public void insert(MemberAccount member) {
        memberJpaRepository.save(toEntity(member));
    }

    /**
     * 회원 정보를 수정한다.
     *
     * @param member 수정할 회원
     */
    public void update(MemberAccount member) {
        memberJpaRepository.save(toEntity(member));
    }

    /**
     * 회원을 삭제한다.
     *
     * @param mno 회원 번호
     */
    public void delete(int mno) {
        memberJpaRepository.deleteById(mno);
    }

    /**
     * 회원 목록을 페이지 단위로 조회한다.
     *
     * @param key 검색 키
     * @param word 검색어
     * @param currentPage 현재 페이지 번호
     * @param itemsPerPage 페이지 크기
     * @return 페이지 결과
     */
    public MemberPageResult search(String key, String word, int currentPage, int itemsPerPage) {
        return memberSearchQueryRepository.search(key, word, currentPage, itemsPerPage);
    }

    /**
     * 회원 즐겨찾기 목록을 조회한다.
     *
     * @param mno 회원 번호
     * @return 즐겨찾기 목록
     */
    public List<MemberFavoriteSummary> getFavorites(int mno) {
        return memberFavoriteQueryRepository.findFavorites(mno);
    }

    /**
     * 회원 즐겨찾기에 아파트를 추가한다.
     *
     * @param mno 회원 번호
     * @param aptSeq 아파트 식별자
     */
    public void addFavorite(int mno, String aptSeq) {
        if (!memberFavoriteJpaRepository.existsByMnoAndAptSeq(mno, aptSeq)) {
            memberFavoriteJpaRepository.save(
                    MemberFavoriteEntity.builder()
                            .mno(mno)
                            .aptSeq(aptSeq)
                            .build());
        }
    }

    /**
     * 회원 즐겨찾기에서 아파트를 삭제한다.
     *
     * @param mno 회원 번호
     * @param aptSeq 아파트 식별자
     */
    public void removeFavorite(int mno, String aptSeq) {
        memberFavoriteJpaRepository.deleteByMnoAndAptSeq(mno, aptSeq);
    }

    private MemberAccount mapToAccount(MemberEntity member) {
        return new MemberAccount(member.getMno(), member.getName(), member.getEmail(), member.getPassword());
    }

    private MemberEntity toEntity(MemberAccount member) {
        return MemberEntity.builder()
                .mno(member.mno() == 0 ? null : member.mno())
                .name(member.name())
                .email(member.email())
                .password(member.password())
                .build();
    }
}


