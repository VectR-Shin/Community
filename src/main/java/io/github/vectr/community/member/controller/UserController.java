package io.github.vectr.community.member.controller;

import io.github.vectr.community.config.security.oauth2.CustomOidcUser;
import io.github.vectr.community.member.api.user.UserCommandService;
import io.github.vectr.community.member.api.user.UserQueryService;
import io.github.vectr.community.member.dto.profile.ProfileUpdateRequestDTO;
import io.github.vectr.community.member.dto.profile.ProfileUpdateResponseDTO;
import io.github.vectr.community.member.dto.user.UserResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    // 내 정보 조회
    @GetMapping("/me")
    public UserResponseDTO getMyInfo(@AuthenticationPrincipal CustomOidcUser principal) {
        return userQueryService.getUserInfo(principal.getMemberId());
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdrawUser(
            @AuthenticationPrincipal CustomOidcUser principal,
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        // 1. 회원 탈퇴 처리
        userCommandService.withdrawUser(
                principal.getMemberId(),
                principal.getProvider(),
                principal.getProviderSub()
        );

        // 2. Community 서비스 세션 정리 및 로그아웃
        logoutHandler.logout(request, response, authentication);
    }
}
