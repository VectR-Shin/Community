package io.github.vectr.community.community.api.board;

public interface BoardManagerCommandService {
    // 특정 사용자의 모든 게시판 관리자 정보 삭제
    void deleteAllBoardManagerByMemberId(Long memberId);
}
