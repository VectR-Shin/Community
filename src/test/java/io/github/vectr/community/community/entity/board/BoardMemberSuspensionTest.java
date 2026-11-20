package io.github.vectr.community.community.entity.board;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.community.enums.BoardSuspensionReason;
import io.github.vectr.community.community.exception.BoardSuspensionErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoardMemberSuspensionTest {

    @Test
    @DisplayName("게시판 회원 정지를 정상적으로 생성한다")
    void createBoardMemberSuspension() {
        // given
        Board board = createDefaultBoard();
        Long memberId = 1L;
        BoardSuspensionReason reason = BoardSuspensionReason.OTHER;
        Instant endsAt = Instant.now().plus(7, ChronoUnit.DAYS);
        Long createdBy = 2L;

        // when
        BoardMemberSuspension suspension = new BoardMemberSuspension(
                board,
                memberId,
                reason,
                endsAt,
                createdBy
        );

        // then
        assertThat(suspension.getBoard()).isEqualTo(board);
        assertThat(suspension.getMemberId()).isEqualTo(memberId);
        assertThat(suspension.getReason()).isEqualTo(reason);
        assertThat(suspension.getEndsAt()).isEqualTo(endsAt);
        assertThat(suspension.getCreatedBy()).isEqualTo(createdBy);
        assertThat(suspension.getLiftedBy()).isNull();
        assertThat(suspension.getLiftedAt()).isNull();
        assertThat(suspension.isLifted()).isFalse();
        assertThat(suspension.isExpired()).isFalse();
    }

    @Test
    @DisplayName("종료 시각이 null이면 영구 정지로 생성한다")
    void createPermanentSuspension() {
        // given
        Instant endsAt = null;

        // when
        BoardMemberSuspension suspension = createDefaultSuspension(endsAt);

        // then
        assertThat(suspension.getEndsAt()).isNull();
        assertThat(suspension.isExpired()).isFalse();
    }

    @Test
    @DisplayName("게시판이 null이면 예외가 발생한다")
    void createWithNullBoard() {
        assertThatNullPointerException()
                .isThrownBy(() -> new BoardMemberSuspension(
                        null,
                        1L,
                        BoardSuspensionReason.OTHER,
                        Instant.now().plus(7, ChronoUnit.DAYS),
                        2L
                ));
    }

    @Test
    @DisplayName("회원 ID가 null이면 예외가 발생한다")
    void createWithNullMemberId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new BoardMemberSuspension(
                        createDefaultBoard(),
                        null,
                        BoardSuspensionReason.OTHER,
                        Instant.now().plus(7, ChronoUnit.DAYS),
                        2L
                ));
    }

    @Test
    @DisplayName("정지 사유가 null이면 예외가 발생한다")
    void createWithNullReason() {
        assertThatNullPointerException()
                .isThrownBy(() -> new BoardMemberSuspension(
                        createDefaultBoard(),
                        1L,
                        null,
                        Instant.now().plus(7, ChronoUnit.DAYS),
                        2L
                ));
    }

    @Test
    @DisplayName("생성자 ID가 null이면 예외가 발생한다")
    void createWithNullCreatedBy() {
        assertThatNullPointerException()
                .isThrownBy(() -> new BoardMemberSuspension(
                        createDefaultBoard(),
                        1L,
                        BoardSuspensionReason.OTHER,
                        Instant.now().plus(7, ChronoUnit.DAYS),
                        null
                ));
    }

    @Test
    @DisplayName("정지를 정상적으로 해제한다")
    void liftSuspension() {
        // given
        BoardMemberSuspension suspension = createDefaultSuspension(
                Instant.now().plus(7, ChronoUnit.DAYS)
        );
        Long liftedBy = 3L;

        // when
        suspension.lift(liftedBy);

        // then
        assertThat(suspension.getLiftedBy()).isEqualTo(liftedBy);
        assertThat(suspension.getLiftedAt()).isNotNull();
        assertThat(suspension.isLifted()).isTrue();
    }

    @Test
    @DisplayName("이미 해제된 정지를 다시 해제하면 예외가 발생한다")
    void liftAlreadyLiftedSuspension() {
        // given
        BoardMemberSuspension suspension = createDefaultSuspension(
                Instant.now().plus(7, ChronoUnit.DAYS)
        );
        suspension.lift(3L);

        // when & then
        assertThatThrownBy(() -> suspension.lift(4L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(BoardSuspensionErrorCode.MEMBER_BOARD_SUSPENSION_ALREADY_LIFTED);
    }

    @Test
    @DisplayName("만료된 정지를 해제하면 예외가 발생한다")
    void liftExpiredSuspension() {
        // given
        BoardMemberSuspension suspension = createDefaultSuspension(
                Instant.now().minus(1, ChronoUnit.DAYS)
        );

        // when & then
        assertThatThrownBy(() -> suspension.lift(3L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(BoardSuspensionErrorCode.MEMBER_NOT_BOARD_SUSPENDED);
    }

    @Test
    @DisplayName("해제자 ID가 null이면 예외가 발생한다")
    void liftWithNullLiftedBy() {
        // given
        BoardMemberSuspension suspension = createDefaultSuspension(
                Instant.now().plus(7, ChronoUnit.DAYS)
        );

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> suspension.lift(null));
    }

    @Test
    @DisplayName("종료 시각이 미래이면 정지가 만료되지 않은 상태다")
    void isNotExpired() {
        // given
        BoardMemberSuspension suspension = createDefaultSuspension(
                Instant.now().plus(7, ChronoUnit.DAYS)
        );

        // when & then
        assertThat(suspension.isExpired()).isFalse();
    }

    @Test
    @DisplayName("종료 시각이 과거이면 정지가 만료된 상태다")
    void isExpired() {
        // given
        BoardMemberSuspension suspension = createDefaultSuspension(
                Instant.now().minus(1, ChronoUnit.DAYS)
        );

        // when & then
        assertThat(suspension.isExpired()).isTrue();
    }

    private Board createDefaultBoard() {
        return new Board(
                new BoardCategory("자유게시판"),
                "테스트 게시판",
                "테스트 설명"
        );
    }

    private BoardMemberSuspension createDefaultSuspension(Instant endsAt) {
        return new BoardMemberSuspension(
                createDefaultBoard(),
                1L,
                BoardSuspensionReason.OTHER,
                endsAt,
                2L
        );
    }
}