
package io.github.vectr.community.member.controller;

import io.github.vectr.community.config.security.oauth2.CustomOidcUser;
import io.github.vectr.community.member.api.user.UserCommandService;
import io.github.vectr.community.member.api.user.UserQueryService;
import io.github.vectr.community.member.dto.profile.ProfileUpdateRequestDTO;
import io.github.vectr.community.member.dto.profile.ProfileUpdateResponseDTO;
import io.github.vectr.community.member.dto.user.UserResponseDTO;
import io.github.vectr.community.member.enums.MemberProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private UserCommandService userCommandService;

    @Mock
    private CustomOidcUser principal;

    @InjectMocks
    private UserController userController;

    private static final Long MEMBER_ID = 1L;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("내 정보 조회")
    class GetMyInfo {

        @Test
        @DisplayName("인증된 사용자의 정보를 조회한다.")
        void getMyInfo() {
            // given
            UserResponseDTO expected = mock(UserResponseDTO.class);

            when(principal.getMemberId()).thenReturn(MEMBER_ID);
            when(userQueryService.getUserInfo(MEMBER_ID))
                    .thenReturn(expected);

            // when
            UserResponseDTO result =
                    userController.getMyInfo(principal);

            // then
            assertThat(result).isSameAs(expected);

            verify(userQueryService).getUserInfo(MEMBER_ID);
            verifyNoInteractions(userCommandService);
        }
    }

    @Nested
    @DisplayName("회원 탈퇴")
    class WithdrawUser {

        @Test
        @DisplayName("회원 탈퇴 후 Community 세션을 정리한다.")
        void withdrawUser() {
            // given
            MemberProvider provider = MemberProvider.GOOGLE;
            String providerSub = "google-sub-123";

            HttpServletRequest request =
                    mock(HttpServletRequest.class);

            HttpServletResponse response =
                    mock(HttpServletResponse.class);

            HttpSession session = mock(HttpSession.class);

            Authentication authentication =
                    mock(Authentication.class);

            when(principal.getMemberId()).thenReturn(MEMBER_ID);
            when(principal.getProvider()).thenReturn(provider);
            when(principal.getProviderSub()).thenReturn(providerSub);

            when(request.getSession(false)).thenReturn(session);

            // when
            userController.withdrawUser(
                    principal,
                    request,
                    response,
                    authentication
            );

            // then
            verify(userCommandService).withdrawUser(
                    MEMBER_ID,
                    provider,
                    providerSub
            );

            // 세션 무효화 확인
            verify(session).invalidate();

            // 로그아웃 시 SecurityContext 정리 확인
            assertThat(SecurityContextHolder.getContext().getAuthentication())
                    .isNull();

            verifyNoInteractions(userQueryService);
        }
    }
}
