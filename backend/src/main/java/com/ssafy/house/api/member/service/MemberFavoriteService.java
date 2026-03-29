package com.ssafy.house.api.member.service;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.house.api.member.dto.response.FavoriteSummaryResponse;
import com.ssafy.house.api.member.dto.response.MemberFavoriteCommandResponse;
import com.ssafy.house.api.member.dto.response.MemberFavoriteListResponse;
import com.ssafy.house.api.member.dto.internal.MemberAccount;
import com.ssafy.house.api.member.dto.internal.MemberFavoriteSummary;
import com.ssafy.house.api.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

/**
 * 현재 로그인한 회원의 즐겨찾기 기능을 처리하는 서비스이다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberFavoriteService {

    private final MemberRepository memberRepository;

    /**
     * 현재 로그인한 회원의 즐겨찾기 목록을 조회한다.
     *
     * @param email 인증된 회원 이메일
     * @return 즐겨찾기 목록 응답
     * @throws SQLException 즐겨찾기 조회 중 예외가 발생한 경우
     * @throws NoSuchElementException 회원이 존재하지 않는 경우
     */
    public MemberFavoriteListResponse getFavorites(String email) throws SQLException {
        MemberAccount member = findMember(email);
        List<FavoriteSummaryResponse> favorites = memberRepository.getFavorites(member.mno()).stream()
                .map(this::mapToResponse)
                .toList();
        return new MemberFavoriteListResponse(favorites);
    }

    /**
     * 현재 로그인한 회원의 즐겨찾기에 아파트를 추가한다.
     *
     * @param email 인증된 회원 이메일
     * @param aptSeq 추가할 아파트 식별자
     * @return 처리 결과 응답
     * @throws SQLException 즐겨찾기 추가 중 예외가 발생한 경우
     * @throws NoSuchElementException 회원이 존재하지 않는 경우
     */
    @Transactional
    public MemberFavoriteCommandResponse addFavorite(String email, String aptSeq) throws SQLException {
        MemberAccount member = findMember(email);
        memberRepository.addFavorite(member.mno(), aptSeq);
        return new MemberFavoriteCommandResponse(aptSeq);
    }

    /**
     * 현재 로그인한 회원의 즐겨찾기에서 아파트를 삭제한다.
     *
     * @param email 인증된 회원 이메일
     * @param aptSeq 삭제할 아파트 식별자
     * @return 처리 결과 응답
     * @throws SQLException 즐겨찾기 삭제 중 예외가 발생한 경우
     * @throws NoSuchElementException 회원이 존재하지 않는 경우
     */
    @Transactional
    public MemberFavoriteCommandResponse removeFavorite(String email, String aptSeq) throws SQLException {
        MemberAccount member = findMember(email);
        memberRepository.removeFavorite(member.mno(), aptSeq);
        return new MemberFavoriteCommandResponse(aptSeq);
    }

    private MemberAccount findMember(String email) {
        MemberAccount member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new NoSuchElementException("회원 정보를 찾을 수 없습니다.");
        }
        return member;
    }

    private FavoriteSummaryResponse mapToResponse(MemberFavoriteSummary favorite) {
        return new FavoriteSummaryResponse(
                favorite.aptSeq(),
                favorite.imgPath(),
                favorite.listingName(),
                favorite.price(),
                favorite.spec(),
                favorite.propertyType(),
                favorite.description());
    }
}

