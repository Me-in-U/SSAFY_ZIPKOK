package com.ssafy.house.global.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ssafy.house.api.member.dto.internal.MemberAccount;
import com.ssafy.house.api.member.repository.MemberRepository;

/**
 * {@link CustomUserDetailsService}의 동작을 검증하는 테스트이다.
 */
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_returnsSpringSecurityUser() {
        when(memberRepository.findByEmail("tester@example.com"))
                .thenReturn(new MemberAccount(1, "테스터", "tester@example.com", "encoded-password"));

        UserDetails result = customUserDetailsService.loadUserByUsername("tester@example.com");

        assertThat(result.getUsername()).isEqualTo("tester@example.com");
        assertThat(result.getPassword()).isEqualTo("encoded-password");
        assertThat(result.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
    }

    @Test
    void loadUserByUsername_throwsWhenMemberMissing() {
        when(memberRepository.findByEmail("missing@example.com")).thenReturn(null);

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("missing@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("No user: missing@example.com");
    }
}

