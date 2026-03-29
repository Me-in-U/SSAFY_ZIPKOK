package com.ssafy.house.api.member.controller;

import java.sql.SQLException;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.house.api.member.dto.request.MemberUpdateRequest;
import com.ssafy.house.api.member.dto.response.MemberPageResponse;
import com.ssafy.house.api.member.dto.response.MemberProfileResponse;
import com.ssafy.house.api.member.service.MemberProfileService;
import com.ssafy.house.api.member.service.MemberQueryService;
import com.ssafy.house.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 현재 로그인한 회원 관련 API를 제공하는 컨트롤러이다.
 */
@RestController
@RequestMapping("/v1/member")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 프로필 기능")
public class MemberController {

    private final MemberProfileService memberProfileService;
    private final MemberQueryService memberQueryService;

    /**
     * 현재 로그인한 회원의 정보를 조회한다.
     *
     * @param authentication 인증 정보
     * @return 회원 프로필 응답
     */
    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 회원의 기본 정보를 조회한다.")
    public BaseResponse<MemberProfileResponse> getMyProfile(Authentication authentication) {
        return BaseResponse.onSuccess(memberProfileService.getCurrentMember(authentication.getName()));
    }

    /**
     * 이메일로 회원 상세 정보를 조회한다.
     *
     * @param email 회원 이메일
     * @return 회원 프로필 응답
     */
    @GetMapping("/{email}")
    @Operation(summary = "회원 상세 조회", description = "이메일로 회원 상세 정보를 조회한다.")
    public BaseResponse<MemberProfileResponse> getMemberByEmail(@PathVariable String email) {
        return BaseResponse.onSuccess(memberQueryService.getMemberByEmail(email));
    }

    /**
     * 회원 목록을 페이지 단위로 조회한다.
     *
     * @param key 검색 키
     * @param word 검색어
     * @param currentPage 현재 페이지 번호
     * @param itemsPerPage 페이지 크기
     * @return 회원 페이지 응답
     */
    @GetMapping
    @Operation(summary = "회원 목록 조회", description = "회원 목록을 페이지 단위로 조회한다.")
    public BaseResponse<MemberPageResponse> getMembers(
            @RequestParam(required = false) String key,
            @RequestParam(required = false) String word,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "5") int itemsPerPage) {
        return BaseResponse.onSuccess(memberQueryService.getMembers(key, word, currentPage, itemsPerPage));
    }

    /**
     * 현재 로그인한 회원의 이름 또는 비밀번호를 수정한다.
     *
     * @param request 수정 요청 데이터
     * @param authentication 인증 정보
     * @return 수정된 회원 프로필 응답
     */
    @PutMapping("/me")
    @Operation(summary = "내 정보 수정", description = "현재 로그인한 회원의 이름 또는 비밀번호를 수정한다.")
    public BaseResponse<MemberProfileResponse> updateMyProfile(
            @RequestBody MemberUpdateRequest request,
            Authentication authentication) throws SQLException {
        return BaseResponse.onSuccess(
                memberProfileService.updateCurrentMember(authentication.getName(), request));
    }

    /**
     * 회원을 삭제한다.
     *
     * @param mno 회원 번호
     * @return 삭제 응답
     */
    @DeleteMapping("/{mno}")
    @Operation(summary = "회원 삭제", description = "회원 번호로 회원을 삭제한다.")
    public BaseResponse<Void> deleteMember(@PathVariable int mno) throws SQLException {
        memberQueryService.deleteMember(mno);
        return BaseResponse.onSuccess();
    }
}
