package io.github.vectr.community.community.controller;

import io.github.vectr.community.common.dto.PageResponseDTO;
import io.github.vectr.community.community.api.board.BoardFavoriteQueryService;
import io.github.vectr.community.community.dto.board.BoardSummaryResponseDTO;
import io.github.vectr.community.config.security.oauth2.CustomOidcUser;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardFavoriteQueryService boardFavoriteQueryService;

    @GetMapping("/users/me/favorite-boards")
    public PageResponseDTO<BoardSummaryResponseDTO> getMyFavoriteBoards(
            @AuthenticationPrincipal CustomOidcUser principal,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "{validation.page.min}") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "{validation.size.min}") @Max(value = 100, message = "{validation.size.max}") int size
    ) {
        return boardFavoriteQueryService.getFavoriteBoards(principal.getMemberId(), page, size);
    }
}
