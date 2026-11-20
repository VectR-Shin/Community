package io.github.vectr.community.community.entity.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoardFavoriteTest {

    private Board createDefaultBoard() {
        return new Board(
                new BoardCategory("자유게시판"),
                "테스트 게시판",
                "테스트 설명"
        );
    }

    @Test
    @DisplayName("BoardFavorite 객체를 정상적으로 생성한다.")
    void createBoardFavorite() {
        // given
        Board board = createDefaultBoard();
        Long memberId = 1L;

        // when
        BoardFavorite favorite = new BoardFavorite(board, memberId);

        // then
        assertThat(favorite.getBoard()).isEqualTo(board);
        assertThat(favorite.getMemberId()).isEqualTo(memberId);
    }

    @Test
    @DisplayName("Board가 null이면 예외가 발생한다.")
    void createBoardFavoriteWithNullBoard() {
        // when & then
        assertThatThrownBy(() -> new BoardFavorite(null, 1L))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("MemberId가 null이면 예외가 발생한다.")
    void createBoardFavoriteWithNullMemberId() {
        // given
        Board board = createDefaultBoard();

        // when & then
        assertThatThrownBy(() -> new BoardFavorite(board, null))
                .isInstanceOf(NullPointerException.class);
    }
}
