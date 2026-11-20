package io.github.vectr.community.member.service.user;

import io.github.vectr.community.config.security.oauth2.keycloak.KeycloakAdminService;
import io.github.vectr.community.member.api.user.UserCommandService;
import io.github.vectr.community.member.enums.MemberProvider;
import io.github.vectr.community.member.repository.ProfileRepository;
import io.github.vectr.community.member.service.withdrawalRecord.UserWithdrawalTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final ProfileRepository profileRepository;

    private final UserWithdrawalTransactionService userWithdrawalTransactionService;

    private final KeycloakAdminService keycloakAdminService;

    /*
     * 현재 사용자 탈퇴 처리
     * - DB 트랜잭션 처리
     * - 재가입 제한 기록 생성
     * - Keycloak Admin API 를 사용하여 Keycloak 계정 삭제
     *   - 자세한 동작은 API Spec(DELETE /users/me) 참조
     * - 세션 정리는 컨트롤러에서 처리
     */
    @Override
    public void withdrawUser(
            Long memberId,
            MemberProvider provider,
            String providerSub
    ) {
        // 1. DB 트랜잭션 처리
        String keycloakSub = userWithdrawalTransactionService.withdraw(
                memberId,
                provider,
                providerSub
        );

        // 2. Keycloak Admin API 를 사용하여 Keycloak 계정 삭제
        try {
            keycloakAdminService.logoutAndDeleteUser(keycloakSub);
        } catch (Exception e) {
            log.error("Keycloak 계정 삭제 실패. memberId={}", memberId, e);
        }
    }
}
