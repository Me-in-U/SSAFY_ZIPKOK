package com.ssafy.house.global.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ssafy.house.api.member.dto.internal.MemberAccount;
import com.ssafy.house.api.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

/**
 * Spring Security 인증에 사용할 사용자 정보를 조회하는 서비스이다.
 */
@Primary
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    /**
     * 이메일로 회원 인증 정보를 조회한다.
     *
     * @param email 회원 이메일
     * @return Spring Security 사용자 정보
     * @throws UsernameNotFoundException 회원이 존재하지 않는 경우
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberAccount member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new UsernameNotFoundException("No user: " + email);
        }
        return User.builder()
                .username(member.email())
                .password(member.password())
                .roles("USER")
                .build();
    }
}

