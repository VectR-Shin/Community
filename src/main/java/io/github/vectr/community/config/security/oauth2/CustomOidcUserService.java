
package io.github.vectr.community.config.security.oauth2;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.config.security.MemberSyncService;
import io.github.vectr.community.config.security.oauth2.auth.CustomOAuth2AuthenticationException;
import io.github.vectr.community.config.security.MemberSyncResult;
import io.github.vectr.community.member.enums.MemberProvider;
import io.github.vectr.community.member.enums.MemberRole;
import io.github.vectr.community.member.exception.MemberErrorCode;
import io.github.vectr.community.member.exception.ProfileErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final MemberSyncService memberSyncService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {

        log.info("OIDC 사용자 정보 로딩 시작");

        // 1. 기본 OIDC 사용자 정보 로딩
        OidcUser oidcUser = super.loadUser(userRequest);

        log.info("OIDC 사용자 정보 로딩 성공");

        // 2. 사용자 식별 정보 추출
        String keycloakSub = oidcUser.getSubject();
        String idToken = oidcUser.getIdToken().getTokenValue();

        // 3. 신규 회원 여부 확인
        boolean existingMember;

        try {
            existingMember =
                    memberSyncService.existsByKeycloakSub(keycloakSub);

        } catch (Exception e) {

            // 기존 회원 여부를 확인하지 못한 경우
            // Keycloak 계정을 삭제하지 않는다.
            log.error(
                    "기존 회원 여부 확인 실패. userId={}",
                    keycloakSub,
                    e
            );

            throw createFailure(
                    "LOGIN_FAILED",
                    e,
                    idToken,
                    keycloakSub,
                    false
            );
        }

        log.info(
                "기존 회원 여부 확인 완료. existingMember={}",
                existingMember
        );

        try {

            // 4. 사용자 정보 추출
            String email = oidcUser.getEmail();

            String providerValue =
                    oidcUser.getClaimAsString("provider");

            // 5. Provider 정보 검증 및 변환
            if (providerValue == null || providerValue.isBlank()) {
                throw createFailure(
                        "INVALID_MEMBER_INFO",
                        null,
                        idToken,
                        keycloakSub,
                        !existingMember
                );
            }

            MemberProvider provider;

            try {
                provider = MemberProvider.valueOf(
                        providerValue.toUpperCase(Locale.ROOT)
                );

            } catch (IllegalArgumentException e) {
                throw createFailure(
                        "INVALID_MEMBER_INFO",
                        e,
                        idToken,
                        keycloakSub,
                        !existingMember
                );
            }

            // 6. 이메일 정규화
            if (email != null) {
                email = email.trim();

                if (email.isBlank()) {
                    email = null;
                } else {
                    email = email.toLowerCase(Locale.ROOT);
                }
            }

            // 모든 Provider 에서 이메일은 필수
            if (email == null) {
                throw createFailure(
                        "INVALID_MEMBER_INFO",
                        null,
                        idToken,
                        keycloakSub,
                        !existingMember
                );
            }

            // 7. Provider 별 사용자 식별 정보 추출
            String providerSub =
                    oidcUser.getClaimAsString("provider_sub");

            // LOCAL 사용자는 이메일과 provider_sub 가 동일해야 함
            if (provider == MemberProvider.LOCAL
                    && (providerSub == null
                    || !email.equals(
                    providerSub.trim().toLowerCase(Locale.ROOT)
            ))) {

                throw createFailure(
                        "INVALID_MEMBER_INFO",
                        null,
                        idToken,
                        keycloakSub,
                        !existingMember
                );
            }

            if (provider == MemberProvider.LOCAL) {
                providerSub = providerSub.trim().toLowerCase(Locale.ROOT);
            }

            log.info("provider: {}", provider);
            log.info(
                    "providerSub 존재 여부: {}",
                    providerSub != null && !providerSub.isBlank()
            );

            // 8. 필수 사용자 정보 검증
            if (keycloakSub == null || keycloakSub.isBlank()
                    || providerSub == null || providerSub.isBlank()) {

                throw createFailure(
                        "INVALID_MEMBER_INFO",
                        null,
                        idToken,
                        keycloakSub,
                        !existingMember
                );
            }

            // 9. 역할 목록 추출
            Object realmAccess =
                    oidcUser.getClaims().get("realm_access");

            if (!(realmAccess instanceof Map<?, ?> realmAccessMap)) {
                throw createFailure(
                        "INVALID_ROLE",
                        null,
                        idToken,
                        keycloakSub,
                        !existingMember
                );
            }

            Object rolesObject = realmAccessMap.get("roles");

            if (!(rolesObject instanceof Collection<?> roles)) {
                throw createFailure(
                        "INVALID_ROLE",
                        null,
                        idToken,
                        keycloakSub,
                        !existingMember
                );
            }

            // 10. 전역 역할 유무 검증
            boolean hasUserRole = roles.contains("USER");
            boolean hasAdminRole = roles.contains("ADMIN");

            if (!hasUserRole && !hasAdminRole) {
                throw createFailure(
                        "INVALID_ROLE",
                        null,
                        idToken,
                        keycloakSub,
                        !existingMember
                );
            }

            // 11. 전역 역할 결정
            MemberRole memberRole = hasAdminRole
                    ? MemberRole.ADMIN
                    : MemberRole.USER;

            // 12. 회원 정보 및 역할 동기화
            MemberSyncResult result =
                    memberSyncService.synchronize(
                            keycloakSub,
                            email,
                            memberRole,
                            provider,
                            providerSub
                    );

            log.info(
                    "회원 동기화 완료. memberId={}, role={}",
                    result.memberId(),
                    result.role()
            );

            // 13. CustomOidcUser 반환
            return new CustomOidcUser(
                    oidcUser,
                    result.memberId(),
                    result.role(),
                    provider,
                    providerSub
            );

        } catch (CustomOAuth2AuthenticationException e) {

            // 이미 변환된 인증 예외는 그대로 전달
            throw e;

        } catch (BusinessException e) {

            // 14. 비즈니스 예외를 인증 실패 코드로 변환
            String errorCode = convertErrorCode(e);

            log.warn(
                    "OIDC 회원 동기화 실패. errorCode={}",
                    errorCode
            );

            throw createFailure(
                    errorCode,
                    e,
                    idToken,
                    keycloakSub,
                    !existingMember
            );

        } catch (Exception e) {

            // 15. 예상하지 못한 예외 처리
            log.error(
                    "OIDC 회원 정보 검증 또는 동기화 실패. userId={}",
                    keycloakSub,
                    e
            );

            throw createFailure(
                    "LOGIN_FAILED",
                    e,
                    idToken,
                    keycloakSub,
                    !existingMember
            );
        }
    }

    /**
     * BusinessException 을 인증 실패 코드로 변환
     */
    private String convertErrorCode(BusinessException e) {

        if (e.getErrorCode()
                == ProfileErrorCode.PROFILE_GENERATION_FAILED) {
            return "PROFILE_GENERATION_FAILED";
        }

        if (e.getErrorCode()
                == MemberErrorCode.REJOIN_RESTRICTED) {
            return "REJOIN_RESTRICTED";
        }

        return "LOGIN_FAILED";
    }

    /**
     * 인증 실패 예외 생성
     */
    private CustomOAuth2AuthenticationException createFailure(
            String errorCode,
            Throwable cause,
            String idToken,
            String keycloakSub,
            boolean deleteKeycloakAccount
    ) {

        // 사용자 ID를 특정할 수 없다면 계정 삭제를 수행하지 않는다.
        boolean shouldDelete =
                deleteKeycloakAccount
                        && keycloakSub != null
                        && !keycloakSub.isBlank();

        return new CustomOAuth2AuthenticationException(
                new OAuth2Error(errorCode),
                cause,
                idToken,
                keycloakSub,
                shouldDelete
        );
    }
}
