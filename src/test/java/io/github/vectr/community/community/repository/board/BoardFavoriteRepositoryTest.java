package io.github.vectr.community.community.repository.board;

import io.github.vectr.community.community.dto.board.BoardSummaryResponseDTO;
import io.github.vectr.community.community.entity.board.Board;
import io.github.vectr.community.community.entity.board.BoardCategory;
import io.github.vectr.community.community.entity.board.BoardFavorite;
import io.github.vectr.community.member.entity.Member;
import io.github.vectr.community.member.enums.MemberProvider;
import io.github.vectr.community.member.enums.MemberRole;
import io.github.vectr.community.member.repository.MemberRepository;
import io.github.vectr.community.common.config.JpaAuditingTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaAuditingTestConfig.class)
class BoardFavoriteRepositoryTest {

    @Autowired
    private BoardFavoriteRepository boardFavoriteRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardCategoryRepository boardCategoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("사용자가 즐겨찾기한 게시판 목록을 DTO로 조회한다.")
    void findFavoriteBoardsByMemberId_success() {
        // given
        Member member = createMember("1");

        BoardCategory category = createCategory("일반");
        Board board = createBoard("일반 게시판", category);

        createFavorite(board, member.getId());

        PageRequest pageable = createPageable(0, 10);

        // when
        Page<BoardSummaryResponseDTO> result =
                boardFavoriteRepository.findFavoriteBoardsByMemberId(
                        member.getId(),
                        pageable
                );

        // then
        assertThat(result.getContent()).hasSize(1);

        BoardSummaryResponseDTO dto = result.getContent().get(0);

        assertThat(dto.boardId()).isEqualTo(board.getId());
        assertThat(dto.boardName()).isEqualTo("일반 게시판");
        assertThat(dto.categoryId()).isEqualTo(category.getId());
        assertThat(dto.categoryName()).isEqualTo("일반");

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("삭제된 게시판은 즐겨찾기 목록에서 제외한다.")
    void findFavoriteBoardsByMemberId_excludeDeletedBoard() {
        // given
        Member member = createMember("2");

        BoardCategory category = createCategory("일반");

        Board normalBoard = createBoard("정상 게시판", category);
        Board deletedBoard = createBoard("삭제 게시판", category);

        createFavorite(normalBoard, member.getId());
        createFavorite(deletedBoard, member.getId());

        deletedBoard.delete();
        boardRepository.saveAndFlush(deletedBoard);

        PageRequest pageable = createPageable(0, 10);

        // when
        Page<BoardSummaryResponseDTO> result =
                boardFavoriteRepository.findFavoriteBoardsByMemberId(
                        member.getId(),
                        pageable
                );

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).boardId())
                .isEqualTo(normalBoard.getId());

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("다른 사용자가 즐겨찾기한 게시판은 조회하지 않는다.")
    void findFavoriteBoardsByMemberId_onlyOwnFavorites() {
        // given
        Member member1 = createMember("3");
        Member member2 = createMember("4");

        BoardCategory category = createCategory("일반");

        Board board1 = createBoard("첫 번째 게시판", category);
        Board board2 = createBoard("두 번째 게시판", category);

        createFavorite(board1, member1.getId());
        createFavorite(board2, member2.getId());

        PageRequest pageable = createPageable(0, 10);

        // when
        Page<BoardSummaryResponseDTO> result =
                boardFavoriteRepository.findFavoriteBoardsByMemberId(
                        member1.getId(),
                        pageable
                );

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).boardId())
                .isEqualTo(board1.getId());

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("요청한 페이지와 크기에 맞게 즐겨찾기 목록을 조회한다.")
    void findFavoriteBoardsByMemberId_paging() {
        // given
        Member member = createMember("5");

        BoardCategory category = createCategory("일반");

        Board board1 = createBoard("게시판 A", category);
        Board board2 = createBoard("게시판 B", category);
        Board board3 = createBoard("게시판 C", category);

        createFavorite(board1, member.getId());
        createFavorite(board2, member.getId());
        createFavorite(board3, member.getId());

        PageRequest pageable = createPageable(1, 2);

        // when
        Page<BoardSummaryResponseDTO> result =
                boardFavoriteRepository.findFavoriteBoardsByMemberId(
                        member.getId(),
                        pageable
                );

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("즐겨찾기 등록일시 오름차순으로 조회한다.")
    void findFavoriteBoardsByMemberId_sortByCreatedAt() {
        // given
        Member member = createMember("6");

        BoardCategory category = createCategory("일반");

        Board board1 = createBoard("첫 번째 게시판", category);
        Board board2 = createBoard("두 번째 게시판", category);

        BoardFavorite favorite1 = createFavorite(board1, member.getId());
        BoardFavorite favorite2 = createFavorite(board2, member.getId());

        ReflectionTestUtils.setField(
                favorite1,
                "createdAt",
                Instant.now().minus(2, ChronoUnit.DAYS)
        );

        ReflectionTestUtils.setField(
                favorite2,
                "createdAt",
                Instant.now().minus(1, ChronoUnit.DAYS)
        );

        boardFavoriteRepository.saveAllAndFlush(
                List.of(favorite1, favorite2)
        );

        PageRequest pageable = createPageable(0, 10);

        // when
        Page<BoardSummaryResponseDTO> result =
                boardFavoriteRepository.findFavoriteBoardsByMemberId(
                        member.getId(),
                        pageable
                );

        // then
        assertThat(result.getContent())
                .extracting(BoardSummaryResponseDTO::boardId)
                .containsExactly(board1.getId(), board2.getId());
    }

    private Member createMember(String suffix) {
        Member member = new Member(
                "test-keycloak-sub-" + suffix,
                "test" + suffix + "@example.com",
                MemberRole.USER
        );

        return memberRepository.saveAndFlush(member);
    }

    private BoardCategory createCategory(String name) {
        return boardCategoryRepository.saveAndFlush(
                new BoardCategory(name)
        );
    }

    private Board createBoard(String name, BoardCategory category) {
        return boardRepository.saveAndFlush(
                new Board(category, name, "테스트 게시판")
        );
    }

    private BoardFavorite createFavorite(Board board, Long memberId) {
        return boardFavoriteRepository.saveAndFlush(
                new BoardFavorite(board, memberId)
        );
    }

    private PageRequest createPageable(int page, int size) {
        return PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.asc("createdAt"),
                        Sort.Order.asc("id")
                )
        );
    }
}