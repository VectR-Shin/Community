package io.github.vectr.community.community.api.board;

import io.github.vectr.community.common.dto.CurrentSuspensionResponseDTO;
import io.github.vectr.community.community.dto.board.BoardSuspensionResponseDTO;

public interface BoardSuspensionQueryService {
    // 사용자에게 현재 적용 중인 특정 게시판 정지 정보 제공
    CurrentSuspensionResponseDTO<BoardSuspensionResponseDTO> getCurrentBoardSuspension(Long memberId, Long boardId);
}
