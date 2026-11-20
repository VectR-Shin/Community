package io.github.vectr.community.community.controller;

import io.github.vectr.community.common.dto.CurrentSuspensionResponseDTO;
import io.github.vectr.community.community.api.board.BoardSuspensionQueryService;
import io.github.vectr.community.community.dto.board.BoardSuspensionResponseDTO;
import io.github.vectr.community.config.security.oauth2.CustomOidcUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BoardSuspensionController {

    private final BoardSuspensionQueryService boardSuspensionQueryService;

    @GetMapping("/board-suspensions/me")
    public CurrentSuspensionResponseDTO<BoardSuspensionResponseDTO> getCurrentBoardSuspension(@AuthenticationPrincipal CustomOidcUser principal, @RequestParam Long boardId) {
        return boardSuspensionQueryService.getCurrentBoardSuspension(principal.getMemberId(), boardId);
    }
}
