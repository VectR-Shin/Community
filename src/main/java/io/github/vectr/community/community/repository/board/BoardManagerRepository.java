package io.github.vectr.community.community.repository.board;

import io.github.vectr.community.community.entity.board.BoardManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardManagerRepository extends JpaRepository<BoardManager, Long> {
    // 회원 식별자로 게시판 관리자 권한 삭제
    void deleteAllByMemberId(Long memberId);
}
