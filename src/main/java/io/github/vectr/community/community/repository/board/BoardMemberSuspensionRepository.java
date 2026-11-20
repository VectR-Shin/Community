package io.github.vectr.community.community.repository.board;

import io.github.vectr.community.community.dto.board.BoardSuspensionResponseDTO;
import io.github.vectr.community.community.entity.board.BoardMemberSuspension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface BoardMemberSuspensionRepository extends JpaRepository<BoardMemberSuspension, Long> {
    // 특정 사용자에게 현재 적용중인 특정 게시판 정지 정보 제공
    @Query("""
        SELECT new io.github.vectr.community.community.dto.board.BoardSuspensionResponseDTO(
            bms.id,
            bms.board.id,
            bms.reason,
            bms.createdAt,
            bms.endsAt
        )
        FROM BoardMemberSuspension as bms
        WHERE bms.memberId = :memberId
          AND bms.board.id = :boardId
          AND bms.createdAt <= :now
          AND bms.liftedAt IS NULL
          AND (bms.endsAt IS NULL OR bms.endsAt > :now)
        """)
    Optional<BoardSuspensionResponseDTO> findCurrentBoardSuspension(
            @Param("memberId") Long memberId,
            @Param("boardId") Long boardId,
            @Param("now") Instant now
    );

    // 특정 사용자가 현재 적용중인 게시판 밴이 있는지 반환
    @Query("""
        SELECT CASE WHEN COUNT(bms) > 0 THEN true ELSE false END
        FROM BoardMemberSuspension bms
        WHERE bms.memberId = :memberId
          AND bms.board.id = :boardId
          AND bms.createdAt <= :now
          AND (bms.endsAt IS NULL OR bms.endsAt > :now)
          AND bms.liftedAt IS NULL
    """)
    boolean existsCurrentBoardSuspension(
            @Param("memberId") Long memberId,
            @Param("boardId") Long boardId,
            @Param("now") Instant now
    );
}
