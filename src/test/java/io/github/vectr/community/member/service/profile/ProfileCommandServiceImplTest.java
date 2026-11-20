package io.github.vectr.community.member.service.profile;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.member.dto.profile.ProfileUpdateRequestDTO;
import io.github.vectr.community.member.dto.profile.ProfileUpdateResponseDTO;
import io.github.vectr.community.member.entity.Profile;
import io.github.vectr.community.member.exception.ProfileErrorCode;
import io.github.vectr.community.member.repository.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileCommandServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private Profile profile;

    @InjectMocks
    private ProfileCommandServiceImpl profileCommandService;

    // 닉네임 수정 성공
    @Test
    void updateProfile_nickname_success() {
        // given
        Long memberId = 1L;
        String nickname = "새로운닉네임";

        ProfileUpdateRequestDTO request =
                new ProfileUpdateRequestDTO(nickname, null);

        when(profileRepository.findByMember_Id(memberId))
                .thenReturn(Optional.of(profile));

        when(profileRepository.existsByNicknameAndMember_IdNot(nickname, memberId))
                .thenReturn(false);

        when(profile.getNickname()).thenReturn(nickname);
        when(profile.isPublicProfile()).thenReturn(true);

        // when
        ProfileUpdateResponseDTO response =
                profileCommandService.updateProfile(memberId, request);

        // then
        verify(profileRepository).findByMember_Id(memberId);
        verify(profileRepository).existsByNicknameAndMember_IdNot(nickname, memberId);
        verify(profile).updateNickname(nickname);
        verify(profile, never()).updatePublic(anyBoolean());

        assertThat(response.nickname()).isEqualTo(nickname);
        assertThat(response.isPublic()).isTrue();
    }

    // 프로필 공개 여부 수정 성공
    @Test
    void updateProfile_isPublic_success() {
        // given
        Long memberId = 1L;
        Boolean isPublic = false;

        ProfileUpdateRequestDTO request =
                new ProfileUpdateRequestDTO(null, isPublic);

        when(profileRepository.findByMember_Id(memberId))
                .thenReturn(Optional.of(profile));

        when(profile.getNickname()).thenReturn("기존닉네임");
        when(profile.isPublicProfile()).thenReturn(isPublic);

        // when
        ProfileUpdateResponseDTO response =
                profileCommandService.updateProfile(memberId, request);

        // then
        verify(profileRepository).findByMember_Id(memberId);
        verify(profileRepository, never())
                .existsByNicknameAndMember_IdNot(anyString(), anyLong());

        verify(profile, never()).updateNickname(anyString());
        verify(profile).updatePublic(isPublic);

        assertThat(response.nickname()).isEqualTo("기존닉네임");
        assertThat(response.isPublic()).isFalse();
    }

    // 닉네임과 공개 여부 모두 수정 성공
    @Test
    void updateProfile_bothFields_success() {
        // given
        Long memberId = 1L;
        String nickname = "새로운닉네임";
        Boolean isPublic = false;

        ProfileUpdateRequestDTO request =
                new ProfileUpdateRequestDTO(nickname, isPublic);

        when(profileRepository.findByMember_Id(memberId))
                .thenReturn(Optional.of(profile));

        when(profileRepository.existsByNicknameAndMember_IdNot(nickname, memberId))
                .thenReturn(false);

        when(profile.getNickname()).thenReturn(nickname);
        when(profile.isPublicProfile()).thenReturn(isPublic);

        // when
        ProfileUpdateResponseDTO response =
                profileCommandService.updateProfile(memberId, request);

        // then
        verify(profileRepository).findByMember_Id(memberId);
        verify(profileRepository).existsByNicknameAndMember_IdNot(nickname, memberId);

        verify(profile).updateNickname(nickname);
        verify(profile).updatePublic(isPublic);

        assertThat(response.nickname()).isEqualTo(nickname);
        assertThat(response.isPublic()).isFalse();
    }

    // 수정할 필드가 없는 경우
    @Test
    void updateProfile_noFields_throwsException() {
        // given
        Long memberId = 1L;

        ProfileUpdateRequestDTO request =
                new ProfileUpdateRequestDTO(null, null);

        // when & then
        assertThatThrownBy(() -> profileCommandService.updateProfile(memberId, request))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(profileRepository);
    }

    // 프로필이 존재하지 않는 경우
    @Test
    void updateProfile_profileNotFound_throwsException() {
        // given
        Long memberId = 1L;

        ProfileUpdateRequestDTO request =
                new ProfileUpdateRequestDTO("새로운닉네임", null);

        when(profileRepository.findByMember_Id(memberId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> profileCommandService.updateProfile(memberId, request))
                .isInstanceOf(BusinessException.class);

        verify(profileRepository).findByMember_Id(memberId);

        verify(profileRepository, never())
                .existsByNicknameAndMember_IdNot(anyString(), anyLong());
    }

    // 닉네임 중복
    @Test
    void updateProfile_duplicateNickname_throwsException() {
        // given
        Long memberId = 1L;
        String nickname = "중복닉네임";

        ProfileUpdateRequestDTO request =
                new ProfileUpdateRequestDTO(nickname, null);

        when(profileRepository.findByMember_Id(memberId))
                .thenReturn(Optional.of(profile));

        when(profileRepository.existsByNicknameAndMember_IdNot(nickname, memberId))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> profileCommandService.updateProfile(memberId, request))
                .isInstanceOf(BusinessException.class);

        verify(profileRepository).findByMember_Id(memberId);
        verify(profileRepository).existsByNicknameAndMember_IdNot(nickname, memberId);

        verify(profile, never()).updateNickname(anyString());
        verify(profile, never()).updatePublic(anyBoolean());
    }
}