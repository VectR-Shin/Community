package io.github.vectr.community.community.api.board;

import io.github.vectr.community.common.dto.PageResponseDTO;
import io.github.vectr.community.community.dto.board.BoardSummaryResponseDTO;

public interface BoardFavoriteQueryService {
    // 사용자가 즐겨찾기 한 게시판 목록을 최신순으로 조회한다.
    // 삭제된 게시판은 조회 대상이 아니다.
    ;PageResponseDTO<BoardSummaryResponseDTO> getFavoriteBoards(Long memberId, int page, int size);
}
