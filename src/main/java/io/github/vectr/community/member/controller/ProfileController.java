package io.github.vectr.community.member.controller;

import io.github.vectr.community.config.security.oauth2.CustomOidcUser;
import io.github.vectr.community.member.api.profile.ProfileCommandService;
import io.github.vectr.community.member.api.profile.ProfileQueryService;
import io.github.vectr.community.member.dto.profile.ProfileResponseDTO;
import io.github.vectr.community.member.dto.profile.ProfileUpdateRequestDTO;
import io.github.vectr.community.member.dto.profile.ProfileUpdateResponseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileQueryService profileQueryService;
    private final ProfileCommandService profileCommandService;

    // 내 프로필 수정
    @PatchMapping("/users/me")
    public ProfileUpdateResponseDTO patchMyProfile(
            @AuthenticationPrincipal CustomOidcUser principal,
            @Valid @RequestBody ProfileUpdateRequestDTO request
    ) {

        return profileCommandService.updateProfile(principal.getMemberId(), request);
    }

    // 사용자 프로필 조회
    @GetMapping("/profiles/{userId}")
    public ProfileResponseDTO getProfile(@PathVariable("userId") @Positive Long memberId) {
        return profileQueryService.getProfile(memberId);
    }
}
