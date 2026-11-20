package io.github.vectr.community.community.entity.board;

import io.github.vectr.community.community.enums.BoardStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoardTest {

    private BoardCategory createDefaultCategory() {
        return new BoardCategory("자유");
    }

    private Board createDefaultBoard() {
        return new Board(
                createDefaultCategory(),
                "테스트 게시판",
                "테스트 설명"
        );
    }

    @Test
    @DisplayName("Board 객체를 정상적으로 생성한다.")
    void createBoard() {
        // given
        BoardCategory category = createDefaultCategory();
        String name = "테스트 게시판";
        String description = "테스트 설명";

        // when
        Board board = new Board(category, name, description);

        // then
        assertThat(board.getCategory()).isEqualTo(category);
        assertThat(board.getName()).isEqualTo(name);
        assertThat(board.getDescription()).isEqualTo(description);
        assertThat(board.getBoardStatus()).isEqualTo(BoardStatus.NORMAL);
        assertThat(board.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("Description이 null이어도 Board 객체를 생성할 수 있다.")
    void createBoardWithNullDescription() {
        // when
        Board board = new Board(
                createDefaultCategory(),
                "테스트 게시판",
                null
        );

        // then
        assertThat(board.getDescription()).isNull();
    }

    @Test
    @DisplayName("Category가 null이면 예외가 발생한다.")
    void createBoardWithNullCategory() {
        assertThatThrownBy(() ->
                new Board(null, "테스트 게시판", "테스트 설명")
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Name이 null이면 예외가 발생한다.")
    void createBoardWithNullName() {
        assertThatThrownBy(() ->
                new Board(createDefaultCategory(), null, "테스트 설명")
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("게시판 카테고리를 정상적으로 변경한다.")
    void changeCategory() {
        // given
        Board board = createDefaultBoard();
        BoardCategory newCategory = new BoardCategory("공지사항");

        // when
        board.changeCategory(newCategory);

        // then
        assertThat(board.getCategory()).isEqualTo(newCategory);
    }

    @Test
    @DisplayName("변경할 카테고리가 null이면 예외가 발생한다.")
    void changeCategoryWithNull() {
        // given
        Board board = createDefaultBoard();

        // when & then
        assertThatThrownBy(() -> board.changeCategory(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("게시판 이름을 정상적으로 변경한다.")
    void changeName() {
        // given
        Board board = createDefaultBoard();
        String newName = "새로운 게시판";

        // when
        board.changeName(newName);

        // then
        assertThat(board.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("변경할 이름이 null이면 예외가 발생한다.")
    void changeNameWithNull() {
        // given
        Board board = createDefaultBoard();

        // when & then
        assertThatThrownBy(() -> board.changeName(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("게시판 설명을 정상적으로 변경한다.")
    void changeDescription() {
        // given
        Board board = createDefaultBoard();
        String newDescription = "새로운 게시판 설명";

        // when
        board.changeDescription(newDescription);

        // then
        assertThat(board.getDescription()).isEqualTo(newDescription);
    }

    @Test
    @DisplayName("게시판 설명을 null로 변경할 수 있다.")
    void changeDescriptionWithNull() {
        // given
        Board board = createDefaultBoard();

        // when
        board.changeDescription(null);

        // then
        assertThat(board.getDescription()).isNull();
    }

    @Test
    @DisplayName("게시판 상태를 정상적으로 변경한다.")
    void changeStatus() {
        // given
        Board board = createDefaultBoard();
        BoardStatus newStatus = BoardStatus.READ_ONLY;

        // when
        board.changeStatus(newStatus);

        // then
        assertThat(board.getBoardStatus()).isEqualTo(newStatus);
    }

    @Test
    @DisplayName("변경할 게시판 상태가 null이면 예외가 발생한다.")
    void changeStatusWithNull() {
        // given
        Board board = createDefaultBoard();

        // when & then
        assertThatThrownBy(() -> board.changeStatus(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("게시판을 정상적으로 삭제한다.")
    void delete() {
        // given
        Board board = createDefaultBoard();

        // when
        board.delete();

        // then
        assertThat(board.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("삭제된 게시판을 정상적으로 복구한다.")
    void restore() {
        // given
        Board board = createDefaultBoard();
        board.delete();

        // when
        board.restore();

        // then
        assertThat(board.getDeletedAt()).isNull();
    }
}