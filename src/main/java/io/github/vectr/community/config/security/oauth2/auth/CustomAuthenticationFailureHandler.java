
package io.github.vectr.community.config.security.oauth2.auth;

import io.github.vectr.community.config.FrontendProperties;
import io.github.vectr.community.config.security.oauth2.keycloak.KeycloakAdminService;
import io.github.vectr.community.config.security.oauth2.keycloak.KeycloakLogoutUrlProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler
        implements AuthenticationFailureHandler {

    private final KeycloakLogoutUrlProvider keycloakLogoutUrlProvider;
    private final KeycloakAdminService keycloakAdminService;

    private final FrontendProperties frontendProperties;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {

        // 1. 오류 코드 및 인증 정보 확인
        String errorCode = "LOGIN_FAILED";
        String idToken = null;
        String keycloakSub = null;
        boolean deleteKeycloakAccount = false;

        if (exception instanceof CustomOAuth2AuthenticationException customException) {

            String code = customException.getError().getErrorCode();

            errorCode = switch (code) {
                case "INVALID_MEMBER_INFO" -> "INVALID_MEMBER_INFO";
                case "INVALID_ROLE" -> "INVALID_ROLE";
                case "PROFILE_GENERATION_FAILED" -> "PROFILE_GENERATION_FAILED";
                case "REJOIN_RESTRICTED" -> "REJOIN_RESTRICTED";
                default -> "LOGIN_FAILED";
            };

            idToken = customException.getIdToken();
            keycloakSub = customException.getKeycloakSub();
            deleteKeycloakAccount =
                    customException.isDeleteKeycloakAccount();
        }

        log.error(
                "OAuth2 인증 실패. errorCode={}, message={}",
                errorCode,
                exception.getMessage(),
                exception
        );

        // 2. SecurityContext 정리
        SecurityContextHolder.clearContext();

        // 3. Community 세션 무효화
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        // 4. JSESSIONID 쿠키 삭제
        Cookie cookie = new Cookie("JSESSIONID", null);

        cookie.setPath(
                request.getContextPath().isEmpty()
                        ? "/"
                        : request.getContextPath()
        );

        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());

        response.addCookie(cookie);

        // 5. 최초 회원 동기화 실패라면 Keycloak 계정 삭제
        if (deleteKeycloakAccount
                && keycloakSub != null
                && !keycloakSub.isBlank()) {

            try {
                log.info(
                        "최초 회원 동기화 실패로 Keycloak 계정 삭제 시작. userId={}",
                        keycloakSub
                );

                keycloakAdminService.logoutAndDeleteUser(keycloakSub);

                log.info(
                        "Keycloak 계정 삭제 완료. userId={}",
                        keycloakSub
                );

            } catch (Exception cleanupException) {

                log.error(
                        "Keycloak 계정 삭제 실패. userId={}",
                        keycloakSub,
                        cleanupException
                );
            }
        }

        // 6. Keycloak 로그아웃 (재가입 방지 등)
        if (idToken != null && !idToken.isBlank()) {

            log.info("인증 실패로 인한 Keycloak 로그아웃 처리");

            String logoutUrl =
                    keycloakLogoutUrlProvider.createLogoutUrl(idToken, errorCode);

            response.sendRedirect(logoutUrl);
            return;
        }

        // 7. ID Token 이 없다면 메인 페이지로 이동 (keycloak 로그인 실패 등)
        log.warn("ID Token 이 없어 Keycloak 로그아웃을 수행할 수 없습니다.");

        response.sendRedirect(
                UriComponentsBuilder
                        .fromUriString(frontendProperties.url())
                        .path("/")
                        .queryParam("error", errorCode)
                        .build()
                        .encode()
                        .toUriString()
        );
    }
}
