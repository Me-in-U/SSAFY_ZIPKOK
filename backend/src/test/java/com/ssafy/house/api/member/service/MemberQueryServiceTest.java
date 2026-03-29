package com.ssafy.house.api.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.house.api.member.dto.internal.MemberAccount;
import com.ssafy.house.api.member.dto.internal.MemberPageResult;
import com.ssafy.house.api.member.repository.MemberRepository;
import com.ssafy.house.api.member.dto.response.MemberPageResponse;
import com.ssafy.house.api.member.dto.response.MemberProfileResponse;

@ExtendWith(MockitoExtension.class)
class MemberQueryServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberQueryService memberQueryService;

    @Test
    void getMemberByEmail_mapsLegacyMemberToResponse() {
        when(memberRepository.findByEmail("tester@example.com")).thenReturn(
                new MemberAccount(1, "테스터", "tester@example.com", null));

        MemberProfileResponse response = memberQueryService.getMemberByEmail("tester@example.com");

        assertThat(response.mno()).isEqualTo(1);
        assertThat(response.email()).isEqualTo("tester@example.com");
    }

    @Test
    void getMembers_mapsLegacyPageToResponse() {
        MemberPageResult page = new MemberPageResult(
                List.of(new MemberAccount(1, "테스터", "tester@example.com", null)),
                2,
                5,
                11,
                3,
                true,
                true,
                1,
                3);
        when(memberRepository.search("email", "tester", 2, 5)).thenReturn(page);

        MemberPageResponse response = memberQueryService.getMembers("2", "tester", 2, 5);

        assertThat(response.currentPage()).isEqualTo(2);
        assertThat(response.totalItems()).isEqualTo(11);
        assertThat(response.members()).hasSize(1);
    }

    @Test
    void deleteMember_delegatesToRepository() throws SQLException {
        memberQueryService.deleteMember(7);

        verify(memberRepository).delete(7);
    }
}

