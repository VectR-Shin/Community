package io.github.vectr.community.community.service.suspension;

import io.github.vectr.community.common.dto.CurrentSuspensionResponseDTO;
import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.community.dto.board.BoardSuspensionResponseDTO;
import io.github.vectr.community.community.repository.board.BoardMemberSuspensionRepository;
import io.github.vectr.community.community.repository.board.BoardRepository;
import io.github.vectr.community.community.service.board.BoardSuspensionQueryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardSuspensionQueryServiceImplTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardMemberSuspensionRepository boardMemberSuspensionRepository;

    @InjectMocks
    private BoardSuspensionQueryServiceImpl boardSuspensionQueryService;

    // 현재 적용 중인 게시판 정지 정보가 존재하는 경우
    @Test
    void getCurrentBoardSuspension_현재_게시판_정지_정보가_존재하면_반환한다() {

        // given
        Long memberId = 1L;
        Long boardId = 10L;

        BoardSuspensionResponseDTO suspension = mock(BoardSuspensionResponseDTO.class);

        when(boardRepository.existsByIdAndDeletedAtIsNull(boardId))
                .thenReturn(true);

        when(boardMemberSuspensionRepository.findCurrentBoardSuspension(
                eq(memberId),
                eq(boardId),
                any(Instant.class)
        )).thenReturn(Optional.of(suspension));

        // when
        CurrentSuspensionResponseDTO<BoardSuspensionResponseDTO> response =
                boardSuspensionQueryService.getCurrentBoardSuspension(memberId, boardId);

        // then
        assertNotNull(response);
        assertSame(suspension, response.suspension());

        verify(boardRepository).existsByIdAndDeletedAtIsNull(boardId);
        verify(boardMemberSuspensionRepository).findCurrentBoardSuspension(
                eq(memberId),
                eq(boardId),
                any(Instant.class)
        );
    }

    // 현재 적용 중인 게시판 정지 정보가 없는 경우
    @Test
    void getCurrentBoardSuspension_현재_게시판_정지_정보가_없으면_null을_반환한다() {

        // given
        Long memberId = 1L;
        Long boardId = 10L;

        when(boardRepository.existsByIdAndDeletedAtIsNull(boardId))
                .thenReturn(true);

        when(boardMemberSuspensionRepository.findCurrentBoardSuspension(
                eq(memberId),
                eq(boardId),
                any(Instant.class)
        )).thenReturn(Optional.empty());

        // when
        CurrentSuspensionResponseDTO<BoardSuspensionResponseDTO> response =
                boardSuspensionQueryService.getCurrentBoardSuspension(memberId, boardId);

        // then
        assertNotNull(response);
        assertNull(response.suspension());

        verify(boardRepository).existsByIdAndDeletedAtIsNull(boardId);
        verify(boardMemberSuspensionRepository).findCurrentBoardSuspension(
                eq(memberId),
                eq(boardId),
                any(Instant.class)
        );
    }

    // 게시판이 존재하지 않거나 삭제된 경우
    @Test
    void getCurrentBoardSuspension_유효하지_않은_게시판이면_예외를_발생한다() {

        // given
        Long memberId = 1L;
        Long boardId = 10L;

        when(boardRepository.existsByIdAndDeletedAtIsNull(boardId))
                .thenReturn(false);

        // when
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> boardSuspensionQueryService.getCurrentBoardSuspension(memberId, boardId)
        );

        // then
        verify(boardRepository).existsByIdAndDeletedAtIsNull(boardId);
        verifyNoInteractions(boardMemberSuspensionRepository);
    }
}