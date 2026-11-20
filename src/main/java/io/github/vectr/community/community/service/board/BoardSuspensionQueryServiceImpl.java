package io.github.vectr.community.community.service.board;

import io.github.vectr.community.common.dto.CurrentSuspensionResponseDTO;
import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.community.api.board.BoardSuspensionQueryService;
import io.github.vectr.community.community.dto.board.BoardSuspensionResponseDTO;
import io.github.vectr.community.community.exception.BoardErrorCode;
import io.github.vectr.community.community.repository.board.BoardMemberSuspensionRepository;
import io.github.vectr.community.community.repository.board.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardSuspensionQueryServiceImpl implements BoardSuspensionQueryService {

    private final BoardRepository boardRepository;
    private final BoardMemberSuspensionRepository boardMemberSuspensionRepository;

    // 사용자에게 현재 적용 중인 특정 게시판 정지 정보 제공
    @Override
    public CurrentSuspensionResponseDTO<BoardSuspensionResponseDTO> getCurrentBoardSuspension(Long memberId, Long boardId) {

        // 게시판 존재 여부 확인
        if (!boardRepository.existsByIdAndDeletedAtIsNull(boardId)) {
            throw new BusinessException(BoardErrorCode.BOARD_NOT_FOUND);
        }

        // 현재 적용 중인 게시판 정지 정보 조회
        BoardSuspensionResponseDTO boardSuspensionResponseDTO
                = boardMemberSuspensionRepository.findCurrentBoardSuspension(memberId, boardId, Instant.now())
                .orElse(null);

        return new CurrentSuspensionResponseDTO<>(boardSuspensionResponseDTO);
    }
}
