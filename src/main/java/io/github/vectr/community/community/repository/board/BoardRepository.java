package io.github.vectr.community.community.repository.board;

import io.github.vectr.community.community.entity.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 삭제되지 않은 게시판이 존재하는지 확인
    boolean existsByIdAndDeletedAtIsNull(Long boardId);
}
