package com.ssafy.house.api.member.service;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ssafy.house.api.member.dto.request.MemberUpdateRequest;
import com.ssafy.house.api.member.dto.response.MemberProfileResponse;
import com.ssafy.house.api.member.dto.internal.MemberAccount;
import com.ssafy.house.api.member.repository.MemberRepository;
import com.ssafy.house.global.exception.CurrentPasswordMismatchException;

import lombok.RequiredArgsConstructor;

/**
 * 현재 로그인한 회원의 프로필 조회와 수정을 처리하는 서비스이다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberProfileService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 현재 로그인한 회원의 정보를 조회한다.
     *
     * @param email 인증된 회원 이메일
     * @return 회원 프로필 응답
     * @throws NoSuchElementException 회원이 존재하지 않는 경우
     */
    public MemberProfileResponse getCurrentMember(String email) {
        return mapToProfile(findMember(email));
    }

    /**
     * 현재 로그인한 회원의 이름 또는 비밀번호를 수정한다.
     *
     * @param email 인증된 회원 이메일
     * @param request 수정 요청 데이터
     * @return 수정된 회원 프로필 응답
     * @throws SQLException 회원 수정 중 예외가 발생한 경우
     * @throws NoSuchElementException 회원이 존재하지 않는 경우
     * @throws CurrentPasswordMismatchException 현재 비밀번호 검증에 실패한 경우
     */
    @Transactional
    public MemberProfileResponse updateCurrentMember(String email, MemberUpdateRequest request)
            throws SQLException {
        MemberAccount member = findMember(email);
        String updatedName = member.name();
        String updatedPassword = member.password();
        boolean changed = false;

        if (StringUtils.hasText(request.newPassword())) {
            validateCurrentPassword(request.currentPassword(), member.password());
            updatedPassword = passwordEncoder.encode(request.newPassword());
            changed = true;
        }

        if (StringUtils.hasText(request.name())) {
            updatedName = request.name();
            changed = true;
        }

        MemberAccount updatedMember =
                new MemberAccount(member.mno(), updatedName, member.email(), updatedPassword);
        if (changed) {
            memberRepository.update(updatedMember);
        }

        return mapToProfile(updatedMember);
    }

    private MemberAccount findMember(String email) {
        MemberAccount member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new NoSuchElementException("회원 정보를 찾을 수 없습니다.");
        }
        return member;
    }

    private void validateCurrentPassword(String currentPassword, String encodedPassword) {
        if (!StringUtils.hasText(currentPassword)
                || !passwordEncoder.matches(currentPassword, encodedPassword)) {
            throw new CurrentPasswordMismatchException("현재 비밀번호가 일치하지 않습니다.");
        }
    }

    private MemberProfileResponse mapToProfile(MemberAccount member) {
        return new MemberProfileResponse(member.mno(), member.name(), member.email());
    }
}

