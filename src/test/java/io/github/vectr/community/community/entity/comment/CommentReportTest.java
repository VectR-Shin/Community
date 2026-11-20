package io.github.vectr.community.community.entity.comment;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.community.entity.board.Board;
import io.github.vectr.community.community.entity.board.BoardCategory;
import io.github.vectr.community.community.entity.post.Post;
import io.github.vectr.community.community.enums.PostType;
import io.github.vectr.community.community.enums.ReportProcessStatus;
import io.github.vectr.community.community.enums.ReportReason;
import io.github.vectr.community.community.exception.ReportErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


class CommentReportTest {

    @Test
    @DisplayName("댓글 신고를 정상적으로 생성한다")
    void createCommentReport() {
        // given
        Comment comment = createDefaultComment();
        Long memberId = 1L;
        String content = "신고 상세 내용";
        ReportReason reason = ReportReason.OTHER;

        // when
        CommentReport report = new CommentReport(
                comment,
                memberId,
                content,
                reason
        );

        // then
        assertThat(report.getComment()).isEqualTo(comment);
        assertThat(report.getMemberId()).isEqualTo(memberId);
        assertThat(report.getContent()).isEqualTo(content);
        assertThat(report.getReason()).isEqualTo(reason);
        assertThat(report.getProcessStatus()).isEqualTo(ReportProcessStatus.PENDING);
        assertThat(report.getProcessedAt()).isNull();
        assertThat(report.getProcessedBy()).isNull();
    }

    @Test
    @DisplayName("신고 내용이 null이어도 정상적으로 생성한다")
    void createWithNullContent() {
        // when
        CommentReport report = new CommentReport(
                createDefaultComment(),
                1L,
                null,
                ReportReason.OTHER
        );

        // then
        assertThat(report.getContent()).isNull();
    }

    @Test
    @DisplayName("댓글이 null이면 예외가 발생한다")
    void createWithNullComment() {
        assertThatNullPointerException()
                .isThrownBy(() -> new CommentReport(
                        null,
                        1L,
                        "신고 상세 내용",
                        ReportReason.OTHER
                ));
    }

    @Test
    @DisplayName("신고자 ID가 null이면 예외가 발생한다")
    void createWithNullMemberId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new CommentReport(
                        createDefaultComment(),
                        null,
                        "신고 상세 내용",
                        ReportReason.OTHER
                ));
    }

    @Test
    @DisplayName("신고 사유가 null이면 예외가 발생한다")
    void createWithNullReason() {
        assertThatNullPointerException()
                .isThrownBy(() -> new CommentReport(
                        createDefaultComment(),
                        1L,
                        "신고 상세 내용",
                        null
                ));
    }

    @Test
    @DisplayName("신고를 정상적으로 처리한다")
    void processReport() {
        // given
        CommentReport report = createDefaultCommentReport();
        Long processedBy = 2L;

        // when
        report.process(processedBy);

        // then
        assertThat(report.getProcessStatus()).isEqualTo(ReportProcessStatus.PROCESSED);
        assertThat(report.getProcessedBy()).isEqualTo(processedBy);
        assertThat(report.getProcessedAt()).isNotNull();
    }

    @Test
    @DisplayName("이미 처리된 신고를 다시 처리하면 예외가 발생한다")
    void processAlreadyProcessedReport() {
        // given
        CommentReport report = createDefaultCommentReport();
        report.process(2L);

        // when & then
        assertThatThrownBy(() -> report.process(3L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReportErrorCode.REPORT_ALREADY_PROCESSED);
    }

    @Test
    @DisplayName("이미 반려된 신고를 처리하면 예외가 발생한다")
    void processRejectedReport() {
        // given
        CommentReport report = createDefaultCommentReport();
        report.reject(2L);

        // when & then
        assertThatThrownBy(() -> report.process(3L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReportErrorCode.REPORT_ALREADY_PROCESSED);
    }

    @Test
    @DisplayName("처리자 ID가 null이면 예외가 발생한다")
    void processWithNullProcessedBy() {
        // given
        CommentReport report = createDefaultCommentReport();

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> report.process(null));
    }

    @Test
    @DisplayName("이미 처리된 신고에 null인 처리자 ID를 전달하면 예외가 발생한다")
    void processAlreadyProcessedReportWithNullProcessedBy() {
        // given
        CommentReport report = createDefaultCommentReport();
        report.process(2L);

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> report.process(null));
    }

    @Test
    @DisplayName("신고를 정상적으로 반려한다")
    void rejectReport() {
        // given
        CommentReport report = createDefaultCommentReport();
        Long processedBy = 2L;

        // when
        report.reject(processedBy);

        // then
        assertThat(report.getProcessStatus()).isEqualTo(ReportProcessStatus.REJECTED);
        assertThat(report.getProcessedBy()).isEqualTo(processedBy);
        assertThat(report.getProcessedAt()).isNotNull();
    }

    @Test
    @DisplayName("이미 반려된 신고를 다시 반려하면 예외가 발생한다")
    void rejectAlreadyRejectedReport() {
        // given
        CommentReport report = createDefaultCommentReport();
        report.reject(2L);

        // when & then
        assertThatThrownBy(() -> report.reject(3L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReportErrorCode.REPORT_ALREADY_PROCESSED);
    }

    @Test
    @DisplayName("이미 처리된 신고를 반려하면 예외가 발생한다")
    void rejectProcessedReport() {
        // given
        CommentReport report = createDefaultCommentReport();
        report.process(2L);

        // when & then
        assertThatThrownBy(() -> report.reject(3L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReportErrorCode.REPORT_ALREADY_PROCESSED);
    }

    @Test
    @DisplayName("반려 시 처리자 ID가 null이면 예외가 발생한다")
    void rejectWithNullProcessedBy() {
        // given
        CommentReport report = createDefaultCommentReport();

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> report.reject(null));
    }

    @Test
    @DisplayName("이미 반려된 신고에 null인 처리자 ID를 전달하면 예외가 발생한다")
    void rejectAlreadyRejectedReportWithNullProcessedBy() {
        // given
        CommentReport report = createDefaultCommentReport();
        report.reject(2L);

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> report.reject(null));
    }

    private CommentReport createDefaultCommentReport() {
        return new CommentReport(
                createDefaultComment(),
                1L,
                "신고 상세 내용",
                ReportReason.OTHER
        );
    }

    private Comment createDefaultComment() {
        return new Comment(
                1L,
                createDefaultPost(),
                null,
                "댓글 내용"
        );
    }

    private Post createDefaultPost() {
        return new Post(
                1L,
                createDefaultBoard(),
                "게시글 제목",
                "게시글 내용",
                PostType.POST
        );
    }

    private Board createDefaultBoard() {
        return new Board(
                new BoardCategory("자유게시판"),
                "테스트 게시판",
                "테스트 설명"
        );
    }
}
