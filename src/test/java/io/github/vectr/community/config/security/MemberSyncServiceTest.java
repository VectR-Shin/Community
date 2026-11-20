
package io.github.vectr.community.config.security;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.member.entity.Member;
import io.github.vectr.community.member.enums.MemberProvider;
import io.github.vectr.community.member.enums.MemberRole;
import io.github.vectr.community.member.repository.MemberRepository;
import io.github.vectr.community.member.repository.ProfileRepository;
import io.github.vectr.community.member.repository.WithdrawalRecordRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberSyncServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private WithdrawalRecordRepository withdrawalRecordRepository;

    @Mock
    private NicknameGenerator nicknameGenerator;

    @Mock
    private HmacService hmacService;

    @InjectMocks
    private MemberSyncService memberSyncService;

    private static final String KEYCLOAK_SUB = "keycloak-sub-123";
    private static final String EMAIL = "test@example.com";
    private static final String EMAIL_HASH = "email-hash";
    private static final String PROVIDER_SUB = "provider-sub-123";
    private static final String PROVIDER_SUB_HASH = "provider-sub-hash";

    @Test
    @DisplayName("기존 회원이 존재하면 회원 정보를 반환한다")
    void synchronize_existingMember() {
        // given
        Member member = mock(Member.class);

        when(memberRepository.findByKeycloakSub(KEYCLOAK_SUB))
                .thenReturn(Optional.of(member));
        when(member.isDeleted()).thenReturn(false);
        when(member.getRole()).thenReturn(MemberRole.USER);
        when(member.getId()).thenReturn(1L);

        // when
        MemberSyncResult result = memberSyncService.synchronize(
                KEYCLOAK_SUB,
                EMAIL,
                MemberRole.USER,
                MemberProvider.GOOGLE,
                PROVIDER_SUB
        );

        // then
        assertThat(result.memberId()).isEqualTo(1L);
        assertThat(result.role()).isEqualTo(MemberRole.USER);

        verify(member, never()).updateRole(any());
        verify(memberRepository, never()).save(any());
        verify(profileRepository, never()).save(any());
        verifyNoInteractions(nicknameGenerator);
    }

    @Test
    @DisplayName("기존 회원의 권한이 변경되면 DB 권한을 동기화한다")
    void synchronize_updateRole() {
        // given
        Member member = mock(Member.class);

        when(memberRepository.findByKeycloakSub(KEYCLOAK_SUB))
                .thenReturn(Optional.of(member));
        when(member.isDeleted()).thenReturn(false);
        when(member.getRole()).thenReturn(MemberRole.USER);
        when(member.getId()).thenReturn(1L);

        // when
        MemberSyncResult result = memberSyncService.synchronize(
                KEYCLOAK_SUB,
                EMAIL,
                MemberRole.ADMIN,
                MemberProvider.GOOGLE,
                PROVIDER_SUB
        );

        // then
        verify(member).updateRole(MemberRole.ADMIN);
        assertThat(result.memberId()).isEqualTo(1L);

        verify(memberRepository, never()).save(any());
        verify(profileRepository, never()).save(any());
    }

    @Test
    @DisplayName("기존 회원이 탈퇴한 상태라면 재가입 제한 예외를 발생시킨다")
    void synchronize_deletedMember() {
        // given
        Member member = mock(Member.class);

        when(memberRepository.findByKeycloakSub(KEYCLOAK_SUB))
                .thenReturn(Optional.of(member));
        when(member.isDeleted()).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> memberSyncService.synchronize(
                KEYCLOAK_SUB,
                EMAIL,
                MemberRole.USER,
                MemberProvider.GOOGLE,
                PROVIDER_SUB
        )).isInstanceOf(BusinessException.class);

        verify(memberRepository, never()).save(any());
        verify(profileRepository, never()).save(any());
    }

    @Test
    @DisplayName("이메일이 재가입 제한 대상이면 예외를 발생시킨다")
    void synchronize_emailRestricted() {
        // given
        when(memberRepository.findByKeycloakSub(KEYCLOAK_SUB))
                .thenReturn(Optional.empty());

        when(hmacService.hash("email:" + EMAIL))
                .thenReturn(EMAIL_HASH);

        when(hmacService.hash("provider-sub:" + PROVIDER_SUB))
                .thenReturn(PROVIDER_SUB_HASH);

        when(withdrawalRecordRepository
                .existsByEmailHashAndBlockedUntilAfter(
                        eq(EMAIL_HASH), any(Instant.class)))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> memberSyncService.synchronize(
                KEYCLOAK_SUB,
                EMAIL,
                MemberRole.USER,
                MemberProvider.GOOGLE,
                PROVIDER_SUB
        )).isInstanceOf(BusinessException.class);

        verify(withdrawalRecordRepository, never())
                .existsByProviderAndProviderSubHashAndBlockedUntilAfter(
                        any(), anyString(), any(Instant.class));

        verify(memberRepository, never()).save(any());
        verify(profileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Provider 계정이 재가입 제한 대상이면 예외를 발생시킨다")
    void synchronize_providerRestricted() {
        // given
        when(memberRepository.findByKeycloakSub(KEYCLOAK_SUB))
                .thenReturn(Optional.empty());

        when(hmacService.hash("email:" + EMAIL))
                .thenReturn(EMAIL_HASH);

        when(hmacService.hash("provider-sub:" + PROVIDER_SUB))
                .thenReturn(PROVIDER_SUB_HASH);

        when(withdrawalRecordRepository
                .existsByEmailHashAndBlockedUntilAfter(
                        eq(EMAIL_HASH), any(Instant.class)))
                .thenReturn(false);

        when(withdrawalRecordRepository
                .existsByProviderAndProviderSubHashAndBlockedUntilAfter(
                        eq(MemberProvider.GOOGLE),
                        eq(PROVIDER_SUB_HASH),
                        any(Instant.class)))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> memberSyncService.synchronize(
                KEYCLOAK_SUB,
                EMAIL,
                MemberRole.USER,
                MemberProvider.GOOGLE,
                PROVIDER_SUB
        )).isInstanceOf(BusinessException.class);

        verify(memberRepository, never()).save(any());
        verify(profileRepository, never()).save(any());
    }

    @Test
    @DisplayName("재가입 제한 대상이 아니면 회원과 프로필을 생성한다")
    void synchronize_createMember() {
        // given
        Member savedMember = mock(Member.class);

        when(memberRepository.findByKeycloakSub(KEYCLOAK_SUB))
                .thenReturn(Optional.empty());

        when(hmacService.hash("email:" + EMAIL))
                .thenReturn(EMAIL_HASH);

        when(hmacService.hash("provider-sub:" + PROVIDER_SUB))
                .thenReturn(PROVIDER_SUB_HASH);

        when(withdrawalRecordRepository
                .existsByEmailHashAndBlockedUntilAfter(
                        eq(EMAIL_HASH), any(Instant.class)))
                .thenReturn(false);

        when(withdrawalRecordRepository
                .existsByProviderAndProviderSubHashAndBlockedUntilAfter(
                        eq(MemberProvider.GOOGLE),
                        eq(PROVIDER_SUB_HASH),
                        any(Instant.class)))
                .thenReturn(false);

        when(nicknameGenerator.generate()).thenReturn("테스트닉네임");

        when(memberRepository.save(any(Member.class)))
                .thenReturn(savedMember);

        when(savedMember.getId()).thenReturn(1L);
        when(savedMember.getRole()).thenReturn(MemberRole.USER);

        // when
        MemberSyncResult result = memberSyncService.synchronize(
                KEYCLOAK_SUB,
                EMAIL,
                MemberRole.USER,
                MemberProvider.GOOGLE,
                PROVIDER_SUB
        );

        // then
        assertThat(result.memberId()).isEqualTo(1L);
        assertThat(result.role()).isEqualTo(MemberRole.USER);

        verify(memberRepository).save(any(Member.class));
        verify(profileRepository).save(any());
        verify(nicknameGenerator).generate();
    }

    @Test
    @DisplayName("LOCAL Provider의 providerSub는 소문자 및 공백 제거 후 해싱한다")
    void synchronize_normalizeLocalProviderSub() {
        // given
        String localProviderSub = "  TEST@EXAMPLE.COM  ";

        when(memberRepository.findByKeycloakSub(KEYCLOAK_SUB))
                .thenReturn(Optional.empty());

        when(hmacService.hash("email:" + EMAIL))
                .thenReturn(EMAIL_HASH);

        when(hmacService.hash("provider-sub:test@example.com"))
                .thenReturn(PROVIDER_SUB_HASH);

        when(withdrawalRecordRepository
                .existsByEmailHashAndBlockedUntilAfter(
                        eq(EMAIL_HASH), any(Instant.class)))
                .thenReturn(false);

        when(withdrawalRecordRepository
                .existsByProviderAndProviderSubHashAndBlockedUntilAfter(
                        eq(MemberProvider.LOCAL),
                        eq(PROVIDER_SUB_HASH),
                        any(Instant.class)))
                .thenReturn(false);

        when(nicknameGenerator.generate()).thenReturn("테스트닉네임");

        Member savedMember = mock(Member.class);
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);
        when(savedMember.getId()).thenReturn(1L);
        when(savedMember.getRole()).thenReturn(MemberRole.USER);

        // when
        memberSyncService.synchronize(
                KEYCLOAK_SUB,
                EMAIL,
                MemberRole.USER,
                MemberProvider.LOCAL,
                localProviderSub
        );

        // then
        verify(hmacService).hash("provider-sub:test@example.com");
    }
}
