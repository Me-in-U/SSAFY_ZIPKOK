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
import com.ssafy.house.api.member.dto.internal.MemberFavoriteSummary;
import com.ssafy.house.api.member.repository.MemberRepository;
import com.ssafy.house.api.member.dto.response.MemberFavoriteCommandResponse;
import com.ssafy.house.api.member.dto.response.MemberFavoriteListResponse;

@ExtendWith(MockitoExtension.class)
class MemberFavoriteServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberFavoriteService memberFavoriteService;

    @Test
    void getFavorites_mapsLegacyFavoritesToResponse() throws SQLException {
        when(memberRepository.findByEmail("tester@example.com")).thenReturn(
                new MemberAccount(9, null, "tester@example.com", null));
        when(memberRepository.getFavorites(9)).thenReturn(List.of(
                new MemberFavoriteSummary("123-456", "/img.png", "반포자이", 120000L, "84A", "APT", "설명")));

        MemberFavoriteListResponse response = memberFavoriteService.getFavorites("tester@example.com");

        assertThat(response.favorites()).hasSize(1);
        assertThat(response.favorites().getFirst().aptSeq()).isEqualTo("123-456");
        assertThat(response.favorites().getFirst().listingName()).isEqualTo("반포자이");
    }

    @Test
    void addFavorite_usesAuthenticatedMembersMno() throws SQLException {
        when(memberRepository.findByEmail("tester@example.com")).thenReturn(
                new MemberAccount(11, null, "tester@example.com", null));

        MemberFavoriteCommandResponse response =
                memberFavoriteService.addFavorite("tester@example.com", "123-456");

        verify(memberRepository).addFavorite(11, "123-456");
        assertThat(response.aptSeq()).isEqualTo("123-456");
    }

    @Test
    void removeFavorite_usesAuthenticatedMembersMno() throws SQLException {
        when(memberRepository.findByEmail("tester@example.com")).thenReturn(
                new MemberAccount(12, null, "tester@example.com", null));

        MemberFavoriteCommandResponse response =
                memberFavoriteService.removeFavorite("tester@example.com", "999-000");

        verify(memberRepository).removeFavorite(12, "999-000");
        assertThat(response.aptSeq()).isEqualTo("999-000");
    }
}

