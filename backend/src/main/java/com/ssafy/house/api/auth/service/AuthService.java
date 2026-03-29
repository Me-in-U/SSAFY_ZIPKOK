package com.ssafy.house.api.auth.service;

import java.sql.SQLException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.house.api.auth.dto.request.LoginRequest;
import com.ssafy.house.api.auth.dto.request.RegisterRequest;
import com.ssafy.house.api.auth.dto.response.LoginResponse;
import com.ssafy.house.api.auth.dto.response.RegisterResponse;
import com.ssafy.house.api.member.dto.internal.MemberAccount;
import com.ssafy.house.api.member.repository.MemberRepository;
import com.ssafy.house.api.member.dto.response.MemberProfileResponse;
import com.ssafy.house.global.common.exception.AuthenticationFailedException;
import com.ssafy.house.global.common.exception.ResourceConflictException;
import com.ssafy.house.global.security.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

/**
 * 인증 관련 애플리케이션 서비스를 제공한다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 이메일과 비밀번호를 검증해 로그인 응답을 생성한다.
     *
     * @param request 로그인 요청 데이터
     * @return 로그인 성공 응답
     */
    public LoginResponse login(LoginRequest request) {
        MemberAccount member = memberRepository.findByEmail(request.email());
        if (member == null || !passwordEncoder.matches(request.password(), member.password())) {
            throw new AuthenticationFailedException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
        return new LoginResponse(jwtUtil.generateToken(member.email()), mapToProfile(member));
    }

    /**
     * 새 회원을 생성하고 응답 DTO로 반환한다.
     *
     * @param request 회원가입 요청 데이터
     * @return 회원가입 성공 응답
     * @throws SQLException 회원 저장 중 예외가 발생한 경우
     */
    @Transactional
    public RegisterResponse register(RegisterRequest request) throws SQLException {
        MemberAccount member = new MemberAccount(
                0,
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password()));
        try {
            memberRepository.insert(member);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceConflictException("이미 존재하는 이메일입니다.");
        }

        MemberAccount savedMember = memberRepository.findByEmail(request.email());
        MemberProfileResponse profile = mapToProfile(savedMember != null ? savedMember : member);
        return new RegisterResponse(profile);
    }

    private MemberProfileResponse mapToProfile(MemberAccount member) {
        return new MemberProfileResponse(member.mno(), member.name(), member.email());
    }
}

