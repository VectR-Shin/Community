package io.github.vectr.community.community.api.board;

public interface BoardFavoriteCommandService {
    // 특정 사용자의 모든 게시판 즐겨찾기 정보 삭제
    void deleteAllBoardFavoriteByMemberId(Long memberId);
}
