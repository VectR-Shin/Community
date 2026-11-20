package io.github.vectr.community.member.service.profile;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.member.dto.profile.ProfileResponseDTO;
import io.github.vectr.community.member.entity.Member;
import io.github.vectr.community.member.entity.Profile;
import io.github.vectr.community.member.repository.MemberRepository;
import io.github.vectr.community.member.repository.ProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileQueryServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileQueryServiceImpl profileQueryService;


    @Test
    @DisplayName("탈퇴했거나 존재하지 않는 회원이면 프로필 조회에 실패한다")
    void getProfileWhenMemberNotFound() {
        // given
        Long memberId = 1L;

        when(memberRepository.existsByIdAndDeletedAtIsNull(memberId))
                .thenReturn(false);

        // when & then
        assertThatThrownBy(() -> profileQueryService.getProfile(memberId))
                .isInstanceOf(BusinessException.class);

        verify(memberRepository).existsByIdAndDeletedAtIsNull(memberId);
        verifyNoInteractions(profileRepository);
    }


    @Test
    @DisplayName("회원은 존재하지만 프로필이 존재하지 않으면 조회에 실패한다")
    void getProfileWhenProfileNotFound() {
        // given
        Long memberId = 1L;

        when(memberRepository.existsByIdAndDeletedAtIsNull(memberId))
                .thenReturn(true);

        when(profileRepository.findByMember_Id(memberId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> profileQueryService.getProfile(memberId))
                .isInstanceOf(BusinessException.class);

        verify(memberRepository).existsByIdAndDeletedAtIsNull(memberId);
        verify(profileRepository).findByMember_Id(memberId);
    }


    @Test
    @DisplayName("비공개 프로필이면 일부 필드를 null로 반환한다")
    void getProfileWhenProfileIsPrivate() {
        // given
        Long memberId = 1L;
        String nickname = "테스트닉네임";

        Profile profile = mock(Profile.class);

        when(memberRepository.existsByIdAndDeletedAtIsNull(memberId))
                .thenReturn(true);

        when(profileRepository.findByMember_Id(memberId))
                .thenReturn(Optional.of(profile));

        when(profile.isPublicProfile())
                .thenReturn(false);

        when(profile.getNickname())
                .thenReturn(nickname);

        // when
        ProfileResponseDTO response = profileQueryService.getProfile(memberId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.nickname()).isEqualTo(nickname);
        assertThat(response.isPublic()).isFalse();
        assertThat(response.userId()).isNull();
        assertThat(response.createdAt()).isNull();

        verify(memberRepository).existsByIdAndDeletedAtIsNull(memberId);
        verify(profileRepository).findByMember_Id(memberId);
    }


    @Test
    @DisplayName("공개 프로필이면 전체 프로필 정보를 반환한다")
    void getProfileWhenProfileIsPublic() {
        // given
        Long memberId = 1L;
        String nickname = "테스트닉네임";
        Instant createdAt = Instant.parse("2026-11-20T00:00:00Z");

        Member member = mock(Member.class);
        Profile profile = mock(Profile.class);

        when(memberRepository.existsByIdAndDeletedAtIsNull(memberId))
                .thenReturn(true);

        when(profileRepository.findByMember_Id(memberId))
                .thenReturn(Optional.of(profile));

        when(profile.isPublicProfile())
                .thenReturn(true);

        when(profile.getMember())
                .thenReturn(member);

        when(member.getId())
                .thenReturn(memberId);

        when(profile.getNickname())
                .thenReturn(nickname);

        when(profile.getCreatedAt())
                .thenReturn(createdAt);

        // when
        ProfileResponseDTO response = profileQueryService.getProfile(memberId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(memberId);
        assertThat(response.nickname()).isEqualTo(nickname);
        assertThat(response.isPublic()).isTrue();
        assertThat(response.createdAt()).isEqualTo(createdAt);

        verify(memberRepository).existsByIdAndDeletedAtIsNull(memberId);
        verify(profileRepository).findByMember_Id(memberId);
    }
}