package io.github.vectr.community.member.controller;

import io.github.vectr.community.config.security.oauth2.CustomOidcUser;
import io.github.vectr.community.member.api.suspension.SuspensionQueryService;
import io.github.vectr.community.common.dto.CurrentSuspensionResponseDTO;
import io.github.vectr.community.member.dto.suspension.SuspensionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/suspensions")
public class SuspensionController {

    private final SuspensionQueryService suspensionQueryService;

    @GetMapping("/me")
    public CurrentSuspensionResponseDTO<SuspensionResponseDTO> getCurrentSuspension(@AuthenticationPrincipal CustomOidcUser principal) {
        return suspensionQueryService.getCurrentSuspension(principal.getMemberId());
    }
}
