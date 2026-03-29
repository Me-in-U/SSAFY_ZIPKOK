package com.ssafy.house.api.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import com.ssafy.house.api.member.dto.request.MemberUpdateRequest;
import com.ssafy.house.api.member.dto.response.MemberProfileResponse;
import com.ssafy.house.api.member.dto.internal.MemberAccount;
import com.ssafy.house.api.member.repository.MemberRepository;
import com.ssafy.house.global.exception.CurrentPasswordMismatchException;

@ExtendWith(MockitoExtension.class)
class MemberProfileServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberProfileService memberProfileService;

    @Test
    void getCurrentMember_returnsMappedProfile() {
        when(memberRepository.findByEmail("tester@example.com")).thenReturn(
                new MemberAccount(7, "테스터", "tester@example.com", null));

        MemberProfileResponse response = memberProfileService.getCurrentMember("tester@example.com");

        assertThat(response.mno()).isEqualTo(7);
        assertThat(response.name()).isEqualTo("테스터");
        assertThat(response.email()).isEqualTo("tester@example.com");
    }

    @Test
    void updateCurrentMember_updatesNameAndPassword() throws SQLException {
        MemberAccount member = new MemberAccount(7, "이전이름", "tester@example.com", "encoded-current");
        when(memberRepository.findByEmail("tester@example.com")).thenReturn(member);
        when(passwordEncoder.matches("current-password", "encoded-current")).thenReturn(true);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new");

        MemberProfileResponse response = memberProfileService.updateCurrentMember(
                "tester@example.com",
                new MemberUpdateRequest("새이름", "current-password", "new-password"));

        ArgumentCaptor<MemberAccount> captor = ArgumentCaptor.forClass(MemberAccount.class);
        verify(memberRepository).update(captor.capture());
        assertThat(captor.getValue().name()).isEqualTo("새이름");
        assertThat(captor.getValue().password()).isEqualTo("encoded-new");
        assertThat(response.name()).isEqualTo("새이름");
    }

    @Test
    void updateCurrentMember_throwsWhenCurrentPasswordDoesNotMatch() {
        when(memberRepository.findByEmail("tester@example.com")).thenReturn(
                new MemberAccount(0, null, "tester@example.com", "encoded-current"));
        when(passwordEncoder.matches("wrong-password", "encoded-current")).thenReturn(false);

        assertThatThrownBy(() -> memberProfileService.updateCurrentMember(
                "tester@example.com",
                new MemberUpdateRequest("새이름", "wrong-password", "new-password")))
                .isInstanceOf(CurrentPasswordMismatchException.class)
                .hasMessage("현재 비밀번호가 일치하지 않습니다.");
    }
}

