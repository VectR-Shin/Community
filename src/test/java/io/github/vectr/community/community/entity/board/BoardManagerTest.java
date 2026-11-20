package io.github.vectr.community.community.entity.board;

import io.github.vectr.community.community.enums.BoardManagerRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoardManagerTest {

    private Board createDefaultBoard() {
        return new Board(
                new BoardCategory("자유게시판"),
                "테스트 게시판",
                "테스트 설명"
        );
    }

    private BoardManager createDefaultBoardManager() {
        return new BoardManager(
                createDefaultBoard(),
                1L,
                BoardManagerRole.SUB_MANAGER
        );
    }

    @Test
    @DisplayName("BoardManager 객체를 정상적으로 생성한다.")
    void createBoardManager() {
        // given
        Board board = createDefaultBoard();
        Long memberId = 1L;
        BoardManagerRole role = BoardManagerRole.SUB_MANAGER;

        // when
        BoardManager manager = new BoardManager(board, memberId, role);

        // then
        assertThat(manager.getBoard()).isEqualTo(board);
        assertThat(manager.getMemberId()).isEqualTo(memberId);
        assertThat(manager.getRole()).isEqualTo(role);
    }

    @Test
    @DisplayName("Board가 null이면 예외가 발생한다.")
    void createBoardManagerWithNullBoard() {
        assertThatThrownBy(() ->
                new BoardManager(null, 1L, BoardManagerRole.SUB_MANAGER)
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("MemberId가 null이면 예외가 발생한다.")
    void createBoardManagerWithNullMemberId() {
        assertThatThrownBy(() ->
                new BoardManager(
                        createDefaultBoard(),
                        null,
                        BoardManagerRole.SUB_MANAGER
                )
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Role이 null이면 예외가 발생한다.")
    void createBoardManagerWithNullRole() {
        assertThatThrownBy(() ->
                new BoardManager(
                        createDefaultBoard(),
                        1L,
                        null
                )
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("담당자 권한을 정상적으로 변경한다.")
    void changeRole() {
        // given
        BoardManager manager = createDefaultBoardManager();
        BoardManagerRole newRole = BoardManagerRole.MANAGER;

        // when
        manager.changeRole(newRole);

        // then
        assertThat(manager.getRole()).isEqualTo(newRole);
    }

    @Test
    @DisplayName("변경할 권한이 null이면 예외가 발생한다.")
    void changeRoleWithNull() {
        // given
        BoardManager manager = createDefaultBoardManager();

        // when & then
        assertThatThrownBy(() -> manager.changeRole(null))
                .isInstanceOf(NullPointerException.class);
    }
}