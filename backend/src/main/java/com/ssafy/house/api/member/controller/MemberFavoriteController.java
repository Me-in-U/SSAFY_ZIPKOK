package com.ssafy.house.api.member.controller;

import java.sql.SQLException;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.api.member.dto.response.MemberFavoriteCommandResponse;
import com.ssafy.house.api.member.dto.response.MemberFavoriteListResponse;
import com.ssafy.house.api.member.service.MemberFavoriteService;
import com.ssafy.house.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 현재 로그인한 회원의 즐겨찾기 API를 제공하는 컨트롤러이다.
 */
@RestController
@RequestMapping("/v1/member/me/favorites")
@RequiredArgsConstructor
@Tag(name = "Member Favorite", description = "회원 즐겨찾기 기능")
public class MemberFavoriteController {

    private final MemberFavoriteService memberFavoriteService;

    /**
     * 현재 로그인한 회원의 즐겨찾기 목록을 조회한다.
     *
     * @param authentication 인증 정보
     * @return 즐겨찾기 목록 응답
     */
    @GetMapping
    @Operation(summary = "내 즐겨찾기 조회", description = "현재 로그인한 회원의 즐겨찾기 목록을 조회한다.")
    public BaseResponse<MemberFavoriteListResponse> getFavorites(Authentication authentication) throws SQLException {
        return BaseResponse.onSuccess(memberFavoriteService.getFavorites(authentication.getName()));
    }

    /**
     * 현재 로그인한 회원의 즐겨찾기에 아파트를 추가한다.
     *
     * @param aptSeq 추가할 아파트 식별자
     * @param authentication 인증 정보
     * @return 처리 결과 응답
     */
    @PostMapping("/{aptSeq}")
    @Operation(summary = "내 즐겨찾기 추가", description = "현재 로그인한 회원의 즐겨찾기에 아파트를 추가한다.")
    public BaseResponse<MemberFavoriteCommandResponse> addFavorite(
            @PathVariable String aptSeq,
            Authentication authentication) throws SQLException {
        return BaseResponse.onSuccess(memberFavoriteService.addFavorite(authentication.getName(), aptSeq));
    }

    /**
     * 현재 로그인한 회원의 즐겨찾기에서 아파트를 삭제한다.
     *
     * @param aptSeq 삭제할 아파트 식별자
     * @param authentication 인증 정보
     * @return 처리 결과 응답
     */
    @DeleteMapping("/{aptSeq}")
    @Operation(summary = "내 즐겨찾기 삭제", description = "현재 로그인한 회원의 즐겨찾기에서 아파트를 삭제한다.")
    public BaseResponse<MemberFavoriteCommandResponse> removeFavorite(
            @PathVariable String aptSeq,
            Authentication authentication) throws SQLException {
        return BaseResponse.onSuccess(memberFavoriteService.removeFavorite(authentication.getName(), aptSeq));
    }
}
