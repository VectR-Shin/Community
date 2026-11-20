package io.github.vectr.community.community.controller.board;

import io.github.vectr.community.common.dto.CurrentSuspensionResponseDTO;
import io.github.vectr.community.community.api.board.BoardSuspensionQueryService;
import io.github.vectr.community.community.controller.BoardSuspensionController;
import io.github.vectr.community.community.dto.board.BoardSuspensionResponseDTO;
import io.github.vectr.community.config.security.oauth2.CustomOidcUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardSuspensionControllerTest {

    @Mock
    private BoardSuspensionQueryService boardSuspensionQueryService;

    @Mock
    private CustomOidcUser principal;

    @InjectMocks
    private BoardSuspensionController boardSuspensionController;

    @Test
    @DisplayName("현재 사용자의 게시판 정지 정보 조회 성공")
    void getCurrentBoardSuspension_success() {
        // given
        Long memberId = 1L;
        Long boardId = 10L;

        when(principal.getMemberId()).thenReturn(memberId);

        BoardSuspensionResponseDTO suspensionDTO =
                mock(BoardSuspensionResponseDTO.class);

        CurrentSuspensionResponseDTO<BoardSuspensionResponseDTO> expected =
                new CurrentSuspensionResponseDTO<>(suspensionDTO);

        when(boardSuspensionQueryService.getCurrentBoardSuspension(memberId, boardId))
                .thenReturn(expected);

        // when
        CurrentSuspensionResponseDTO<BoardSuspensionResponseDTO> result =
                boardSuspensionController.getCurrentBoardSuspension(principal, boardId);

        // then
        assertThat(result).isSameAs(expected);

        verify(principal).getMemberId();
        verify(boardSuspensionQueryService)
                .getCurrentBoardSuspension(memberId, boardId);
    }

    @Test
    @DisplayName("현재 정지 정보가 없는 경우")
    void getCurrentBoardSuspension_noSuspension() {
        // given
        Long memberId = 1L;
        Long boardId = 10L;

        when(principal.getMemberId()).thenReturn(memberId);

        CurrentSuspensionResponseDTO<BoardSuspensionResponseDTO> expected =
                new CurrentSuspensionResponseDTO<>(null);

        when(boardSuspensionQueryService.getCurrentBoardSuspension(memberId, boardId))
                .thenReturn(expected);

        // when
        CurrentSuspensionResponseDTO<BoardSuspensionResponseDTO> result =
                boardSuspensionController.getCurrentBoardSuspension(principal, boardId);

        // then
        assertThat(result).isSameAs(expected);
        verify(boardSuspensionQueryService)
                .getCurrentBoardSuspension(memberId, boardId);
    }
}