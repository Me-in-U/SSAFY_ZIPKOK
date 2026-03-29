package com.ssafy.house.api.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ssafy.house.api.auth.dto.request.LoginRequest;
import com.ssafy.house.api.auth.dto.request.RegisterRequest;
import com.ssafy.house.api.auth.dto.response.LoginResponse;
import com.ssafy.house.api.auth.dto.response.RegisterResponse;
import com.ssafy.house.api.member.dto.internal.MemberAccount;
import com.ssafy.house.api.member.repository.MemberRepository;
import com.ssafy.house.global.common.exception.AuthenticationFailedException;
import com.ssafy.house.global.security.jwt.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_returnsTokenAndUserWhenCredentialsAreValid() {
        MemberAccount member = new MemberAccount(1, "테스터", "tester@example.com", "encoded-password");
        when(memberRepository.findByEmail("tester@example.com")).thenReturn(member);
        when(passwordEncoder.matches("plain-password", "encoded-password")).thenReturn(true);
        when(jwtUtil.generateToken("tester@example.com")).thenReturn("jwt-token");

        LoginResponse response = authService.login(new LoginRequest("tester@example.com", "plain-password"));

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.user().email()).isEqualTo("tester@example.com");
        assertThat(response.user().name()).isEqualTo("테스터");
    }

    @Test
    void login_throwsWhenCredentialsAreInvalid() {
        MemberAccount member = new MemberAccount(0, null, "tester@example.com", "encoded-password");
        when(memberRepository.findByEmail("tester@example.com")).thenReturn(member);
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(
                new LoginRequest("tester@example.com", "wrong-password")))
                .isInstanceOf(AuthenticationFailedException.class)
                .hasMessage("이메일 또는 비밀번호가 일치하지 않습니다.");
    }

    @Test
    void register_encodesPasswordAndReturnsSavedMember() throws SQLException {
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(memberRepository.findByEmail("tester@example.com")).thenReturn(
                new MemberAccount(3, "신규회원", "tester@example.com", "encoded-password"));

        RegisterResponse response =
                authService.register(new RegisterRequest("신규회원", "tester@example.com", "plain-password"));

        ArgumentCaptor<MemberAccount> captor = ArgumentCaptor.forClass(MemberAccount.class);
        verify(memberRepository).insert(captor.capture());
        assertThat(captor.getValue().password()).isEqualTo("encoded-password");
        assertThat(response.member().mno()).isEqualTo(3);
        assertThat(response.member().email()).isEqualTo("tester@example.com");
    }
}

