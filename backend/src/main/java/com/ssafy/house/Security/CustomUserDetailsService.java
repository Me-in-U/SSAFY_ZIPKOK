package com.ssafy.house.Security;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;

import com.ssafy.house.model.service.MemberService;
import com.ssafy.house.model.dto.Member;

@Primary
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        Member member = memberService.selectByEmail(email);
        if (member == null) {
            throw new UsernameNotFoundException("No user: " + email);
        }
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword()) // 이미 Bcrypt 해시
                .roles("USER") // 필요시 member.getRole()
                .build();
    }
}
