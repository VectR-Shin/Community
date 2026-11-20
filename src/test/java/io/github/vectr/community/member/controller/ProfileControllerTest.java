package io.github.vectr.community.member.controller;

import io.github.vectr.community.config.security.oauth2.CustomOidcUser;
import io.github.vectr.community.member.api.profile.ProfileCommandService;
import io.github.vectr.community.member.api.profile.ProfileQueryService;
import io.github.vectr.community.member.dto.profile.ProfileResponseDTO;
import io.github.vectr.community.member.dto.profile.ProfileUpdateRequestDTO;
import io.github.vectr.community.member.dto.profile.ProfileUpdateResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private ProfileQueryService profileQueryService;

    @Mock
    private ProfileCommandService profileCommandService;

    @Mock
    private CustomOidcUser principal;

    @InjectMocks
    private ProfileController profileController;


    @Test
    @DisplayName("내 프로필을 수정한다")
    void patchMyProfile() {
        // given
        Long memberId = 1L;

        ProfileUpdateRequestDTO request = mock(ProfileUpdateRequestDTO.class);
        ProfileUpdateResponseDTO response = mock(ProfileUpdateResponseDTO.class);

        when(principal.getMemberId()).thenReturn(memberId);

        when(profileCommandService.updateProfile(memberId, request))
                .thenReturn(response);

        // when
        ProfileUpdateResponseDTO result =
                profileController.patchMyProfile(principal, request);

        // then
        verify(profileCommandService).updateProfile(memberId, request);
        verifyNoInteractions(profileQueryService);

        org.assertj.core.api.Assertions.assertThat(result).isEqualTo(response);
    }


    @Test
    @DisplayName("사용자 프로필을 조회한다")
    void getProfile() {
        // given
        Long memberId = 2L;

        ProfileResponseDTO response = mock(ProfileResponseDTO.class);

        when(profileQueryService.getProfile(memberId))
                .thenReturn(response);

        // when
        ProfileResponseDTO result = profileController.getProfile(memberId);

        // then
        verify(profileQueryService).getProfile(memberId);
        verifyNoInteractions(profileCommandService);

        org.assertj.core.api.Assertions.assertThat(result).isEqualTo(response);
    }
}