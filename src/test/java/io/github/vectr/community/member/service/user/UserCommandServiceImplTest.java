package io.github.vectr.community.member.service.user;

import io.github.vectr.community.config.security.oauth2.keycloak.KeycloakAdminService;
import io.github.vectr.community.member.enums.MemberProvider;
import io.github.vectr.community.member.service.withdrawalRecord.UserWithdrawalTransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceImplTest {

    @Mock
    private UserWithdrawalTransactionService userWithdrawalTransactionService;

    @Mock
    private KeycloakAdminService keycloakAdminService;

    @InjectMocks
    private UserCommandServiceImpl userCommandService;

    @Test
    @DisplayName("회원 탈퇴 후 Keycloak 계정을 로그아웃 및 삭제한다.")
    void withdrawUser() {
        // given
        Long memberId = 1L;
        MemberProvider provider = MemberProvider.LOCAL;
        String providerSub = "test@example.com";
        String keycloakSub = "keycloak-sub";

        when(userWithdrawalTransactionService.withdraw(
                memberId,
                provider,
                providerSub
        )).thenReturn(keycloakSub);

        // when
        userCommandService.withdrawUser(memberId, provider, providerSub);

        // then
        verify(userWithdrawalTransactionService).withdraw(
                memberId,
                provider,
                providerSub
        );

        verify(keycloakAdminService).logoutAndDeleteUser(keycloakSub);
    }

    @Test
    @DisplayName("Keycloak 계정 삭제에 실패해도 회원 탈퇴 처리는 예외를 전파하지 않는다.")
    void withdrawUserWhenKeycloakDeletionFails() {
        // given
        Long memberId = 1L;
        MemberProvider provider = MemberProvider.LOCAL;
        String providerSub = "test@example.com";
        String keycloakSub = "keycloak-sub";

        when(userWithdrawalTransactionService.withdraw(
                memberId,
                provider,
                providerSub
        )).thenReturn(keycloakSub);

        doThrow(new RuntimeException("Keycloak API failure"))
                .when(keycloakAdminService)
                .logoutAndDeleteUser(keycloakSub);

        // when & then
        assertThatCode(() ->
                userCommandService.withdrawUser(memberId, provider, providerSub)
        ).doesNotThrowAnyException();

        verify(userWithdrawalTransactionService).withdraw(
                memberId,
                provider,
                providerSub
        );

        verify(keycloakAdminService).logoutAndDeleteUser(keycloakSub);
    }

    @Test
    @DisplayName("DB 탈퇴 트랜잭션 처리에 실패하면 Keycloak 계정 삭제를 호출하지 않는다.")
    void withdrawUserWhenDatabaseTransactionFails() {
        // given
        Long memberId = 1L;
        MemberProvider provider = MemberProvider.LOCAL;
        String providerSub = "test@example.com";

        doThrow(new RuntimeException("DB transaction failure"))
                .when(userWithdrawalTransactionService)
                .withdraw(memberId, provider, providerSub);

        // when & then
        assertThatCode(() ->
                userCommandService.withdrawUser(memberId, provider, providerSub)
        ).isInstanceOf(RuntimeException.class);

        verify(userWithdrawalTransactionService).withdraw(
                memberId,
                provider,
                providerSub
        );

        verifyNoInteractions(keycloakAdminService);
    }
}