package io.github.vectr.community.community.repository.board;

import io.github.vectr.community.community.dto.board.BoardSummaryResponseDTO;
import io.github.vectr.community.community.entity.board.BoardFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardFavoriteRepository extends JpaRepository<BoardFavorite, Long> {

    // 특정 사용자의 모든 게시판 즐겨찾기 정보 삭제
    void deleteAllByMemberId(Long memberId);

    // 즐겨찾기 등록한 게시판 목록 조회
    // 삭제된 게시판은 조회 대상이 아니다.
    @Query(
            value = """
                    SELECT new io.github.vectr.community.community.dto.board.BoardSummaryResponseDTO(
                        b.id,
                        b.name,
                        c.id,
                        c.name
                    )
                    FROM BoardFavorite bf
                    JOIN bf.board b
                    JOIN b.category c
                    WHERE bf.memberId = :memberId
                      AND b.deletedAt IS NULL
                    """,
            countQuery = """
                    SELECT COUNT(bf)
                    FROM BoardFavorite bf
                    JOIN bf.board b
                    WHERE bf.memberId = :memberId
                      AND b.deletedAt IS NULL
                    """
    )
    Page<BoardSummaryResponseDTO> findFavoriteBoardsByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}
