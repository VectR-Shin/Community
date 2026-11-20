
package io.github.vectr.community.member.service.user;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.config.security.HmacService;
import io.github.vectr.community.community.api.board.BoardFavoriteCommandService;
import io.github.vectr.community.community.api.board.BoardManagerCommandService;
import io.github.vectr.community.member.config.MemberPolicyProperties;
import io.github.vectr.community.member.entity.Member;
import io.github.vectr.community.member.entity.WithdrawalRecord;
import io.github.vectr.community.member.enums.MemberProvider;
import io.github.vectr.community.member.enums.MemberRole;
import io.github.vectr.community.member.exception.MemberErrorCode;
import io.github.vectr.community.member.repository.BlockRepository;
import io.github.vectr.community.member.repository.MemberRepository;
import io.github.vectr.community.member.repository.ProfileRepository;
import io.github.vectr.community.member.repository.WithdrawalRecordRepository;
import io.github.vectr.community.member.service.withdrawalRecord.UserWithdrawalTransactionService;
import io.github.vectr.community.notification.api.NotificationCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserWithdrawalTransactionServiceTest {

    private static final Long MEMBER_ID = 1L;
    private static final String KEYCLOAK_SUB = "keycloak-sub";
    private static final String EMAIL = " TEST@EXAMPLE.COM ";
    private static final String NORMALIZED_EMAIL = "test@example.com";
    private static final String PROVIDER_SUB = "google-sub";
    private static final String EMAIL_HASH = "email-hash";
    private static final String PROVIDER_SUB_HASH = "provider-sub-hash";

    private static final Duration REJOIN_RESTRICTION_PERIOD =
            Duration.ofDays(30);

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private WithdrawalRecordRepository withdrawalRecordRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private BoardManagerCommandService boardManagerCommandService;

    @Mock
    private BoardFavoriteCommandService boardFavoriteCommandService;

    @Mock
    private NotificationCommandService notificationCommandService;

    @Mock
    private HmacService hmacService;

    private UserWithdrawalTransactionService userWithdrawalTransactionService;

    private MemberPolicyProperties memberPolicyProperties;

    @BeforeEach
    void setUp() {
        MemberPolicyProperties memberPolicyProperties =
                new MemberPolicyProperties(
                        new MemberPolicyProperties.Withdrawal(
                                REJOIN_RESTRICTION_PERIOD
                        )
                );

        userWithdrawalTransactionService =
                new UserWithdrawalTransactionService(
                        memberRepository,
                        withdrawalRecordRepository,
                        profileRepository,
                        blockRepository,
                        boardManagerCommandService,
                        boardFavoriteCommandService,
                        notificationCommandService,
                        hmacService,
                        memberPolicyProperties
                );
    }

    @Test
    @DisplayName("회원 탈퇴를 정상적으로 처리한다.")
    void withdraw_success() {
        // given
        Member member = createMember();

        when(memberRepository.findById(MEMBER_ID))
                .thenReturn(Optional.of(member));

        when(hmacService.hash("email:" + NORMALIZED_EMAIL))
                .thenReturn(EMAIL_HASH);

        when(hmacService.hash("provider-sub:" + PROVIDER_SUB))
                .thenReturn(PROVIDER_SUB_HASH);

        Instant before = Instant.now();

        // when
        String result = userWithdrawalTransactionService.withdraw(
                MEMBER_ID,
                MemberProvider.GOOGLE,
                PROVIDER_SUB
        );

        Instant after = Instant.now();

        // then
        assertThat(result).isEqualTo(KEYCLOAK_SUB);
        assertThat(member.isDeleted()).isTrue();

        ArgumentCaptor<WithdrawalRecord> captor =
                ArgumentCaptor.forClass(WithdrawalRecord.class);

        verify(withdrawalRecordRepository).save(captor.capture());

        WithdrawalRecord savedRecord = captor.getValue();

        assertThat(savedRecord.getEmailHash()).isEqualTo(EMAIL_HASH);
        assertThat(savedRecord.getProvider()).isEqualTo(MemberProvider.GOOGLE);
        assertThat(savedRecord.getProviderSubHash()).isEqualTo(PROVIDER_SUB_HASH);

        assertThat(savedRecord.getBlockedUntil())
                .isBetween(
                        before.plus(REJOIN_RESTRICTION_PERIOD),
                        after.plus(REJOIN_RESTRICTION_PERIOD)
                );

        verify(hmacService).hash("email:" + NORMALIZED_EMAIL);
        verify(hmacService).hash("provider-sub:" + PROVIDER_SUB);

        verify(boardManagerCommandService)
                .deleteAllBoardManagerByMemberId(MEMBER_ID);

        verify(boardFavoriteCommandService)
                .deleteAllBoardFavoriteByMemberId(MEMBER_ID);

        verify(blockRepository)
                .deleteAllByBlocker_IdOrBlocked_Id(MEMBER_ID, MEMBER_ID);

        verify(notificationCommandService)
                .deleteAllNotificationByReceiverId(MEMBER_ID);

        verify(profileRepository)
                .deleteByMember_Id(MEMBER_ID);

        verify(memberRepository).findById(MEMBER_ID);
    }

    @Test
    @DisplayName("존재하지 않는 회원이면 MEMBER_NOT_FOUND 예외가 발생한다.")
    void withdraw_memberNotFound() {
        // given
        when(memberRepository.findById(MEMBER_ID))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                userWithdrawalTransactionService.withdraw(
                        MEMBER_ID,
                        MemberProvider.GOOGLE,
                        PROVIDER_SUB
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        MemberErrorCode.MEMBER_NOT_FOUND
                );

        verify(memberRepository).findById(MEMBER_ID);

        verifyNoInteractions(
                withdrawalRecordRepository,
                profileRepository,
                blockRepository,
                boardManagerCommandService,
                boardFavoriteCommandService,
                notificationCommandService,
                hmacService
        );
    }

    @Test
    @DisplayName("이미 탈퇴한 회원이면 MEMBER_ALREADY_WITHDRAWN 예외가 발생한다.")
    void withdraw_alreadyWithdrawn() {
        // given
        Member member = createMember();
        member.withdraw();

        when(memberRepository.findById(MEMBER_ID))
                .thenReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() ->
                userWithdrawalTransactionService.withdraw(
                        MEMBER_ID,
                        MemberProvider.GOOGLE,
                        PROVIDER_SUB
                )
        )
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue(
                        "errorCode",
                        MemberErrorCode.MEMBER_ALREADY_WITHDRAWN
                );

        verify(memberRepository).findById(MEMBER_ID);

        verifyNoInteractions(
                withdrawalRecordRepository,
                profileRepository,
                blockRepository,
                boardManagerCommandService,
                boardFavoriteCommandService,
                notificationCommandService,
                hmacService
        );
    }

    private Member createMember() {
        return new Member(
                KEYCLOAK_SUB,
                EMAIL,
                MemberRole.USER
        );
    }
}
