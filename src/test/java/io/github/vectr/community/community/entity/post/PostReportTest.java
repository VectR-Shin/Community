package io.github.vectr.community.community.entity.post;

import io.github.vectr.community.community.entity.board.Board;
import io.github.vectr.community.community.entity.board.BoardCategory;
import io.github.vectr.community.community.enums.PostType;
import io.github.vectr.community.community.enums.ReportProcessStatus;
import io.github.vectr.community.community.enums.ReportReason;
import io.github.vectr.community.community.exception.ReportErrorCode;
import io.github.vectr.community.common.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostReportTest {

    @Test
    @DisplayName("게시글 신고를 정상적으로 생성한다")
    void createPostReport() {
        // given
        Post post = createDefaultPost();
        Long memberId = 1L;
        String content = "신고 내용";
        ReportReason reason = ReportReason.OTHER;

        // when
        PostReport report = new PostReport(
                post,
                memberId,
                content,
                reason
        );

        // then
        assertThat(report.getPost()).isEqualTo(post);
        assertThat(report.getMemberId()).isEqualTo(memberId);
        assertThat(report.getContent()).isEqualTo(content);
        assertThat(report.getReason()).isEqualTo(reason);
        assertThat(report.getProcessStatus()).isEqualTo(ReportProcessStatus.PENDING);
        assertThat(report.getProcessedAt()).isNull();
        assertThat(report.getProcessedBy()).isNull();
    }

    @Test
    @DisplayName("신고 내용은 null이어도 정상적으로 생성한다")
    void createPostReportWithNullContent() {
        // when
        PostReport report = new PostReport(
                createDefaultPost(),
                1L,
                null,
                ReportReason.OTHER
        );

        // then
        assertThat(report.getContent()).isNull();
    }

    @Test
    @DisplayName("게시글이 null이면 예외가 발생한다")
    void createWithNullPost() {
        assertThatNullPointerException()
                .isThrownBy(() -> new PostReport(
                        null,
                        1L,
                        "신고 내용",
                        ReportReason.OTHER
                ));
    }

    @Test
    @DisplayName("회원 ID가 null이면 예외가 발생한다")
    void createWithNullMemberId() {
        assertThatNullPointerException()
                .isThrownBy(() -> new PostReport(
                        createDefaultPost(),
                        null,
                        "신고 내용",
                        ReportReason.OTHER
                ));
    }

    @Test
    @DisplayName("신고 사유가 null이면 예외가 발생한다")
    void createWithNullReason() {
        assertThatNullPointerException()
                .isThrownBy(() -> new PostReport(
                        createDefaultPost(),
                        1L,
                        "신고 내용",
                        null
                ));
    }

    @Test
    @DisplayName("신고를 정상적으로 처리한다")
    void process() {
        // given
        PostReport report = createDefaultPostReport();
        Long processedBy = 2L;
        Instant before = Instant.now();

        // when
        report.process(processedBy);

        Instant after = Instant.now();

        // then
        assertThat(report.getProcessStatus()).isEqualTo(ReportProcessStatus.PROCESSED);
        assertThat(report.getProcessedBy()).isEqualTo(processedBy);
        assertThat(report.getProcessedAt()).isNotNull();
        assertThat(report.getProcessedAt()).isBetween(before, after);
    }

    @Test
    @DisplayName("이미 처리된 신고를 다시 처리하면 예외가 발생한다")
    void processAlreadyProcessedReport() {
        // given
        PostReport report = createDefaultPostReport();
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
        PostReport report = createDefaultPostReport();
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
        PostReport report = createDefaultPostReport();

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> report.process(null));
    }

    @Test
    @DisplayName("이미 처리된 신고에 null인 처리자 ID를 전달하면 예외가 발생한다")
    void processAlreadyProcessedReportWithNullProcessedBy() {
        // given
        PostReport report = createDefaultPostReport();
        report.process(2L);

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> report.process(null));
    }

    @Test
    @DisplayName("신고를 정상적으로 반려한다")
    void reject() {
        // given
        PostReport report = createDefaultPostReport();
        Long processedBy = 2L;
        Instant before = Instant.now();

        // when
        report.reject(processedBy);

        Instant after = Instant.now();

        // then
        assertThat(report.getProcessStatus()).isEqualTo(ReportProcessStatus.REJECTED);
        assertThat(report.getProcessedBy()).isEqualTo(processedBy);
        assertThat(report.getProcessedAt()).isNotNull();
        assertThat(report.getProcessedAt()).isBetween(before, after);
    }

    @Test
    @DisplayName("이미 반려된 신고를 다시 반려하면 예외가 발생한다")
    void rejectAlreadyRejectedReport() {
        // given
        PostReport report = createDefaultPostReport();
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
        PostReport report = createDefaultPostReport();
        report.process(2L);

        // when & then
        assertThatThrownBy(() -> report.reject(3L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReportErrorCode.REPORT_ALREADY_PROCESSED);
    }

    @Test
    @DisplayName("처리자 ID가 null이면 예외가 발생한다")
    void rejectWithNullProcessedBy() {
        // given
        PostReport report = createDefaultPostReport();

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> report.reject(null));
    }

    @Test
    @DisplayName("이미 반려된 신고에 null인 처리자 ID를 전달하면 예외가 발생한다")
    void rejectAlreadyRejectedReportWithNullProcessedBy() {
        // given
        PostReport report = createDefaultPostReport();
        report.reject(2L);

        // when & then
        assertThatNullPointerException()
                .isThrownBy(() -> report.reject(null));
    }

    private PostReport createDefaultPostReport() {
        return new PostReport(
                createDefaultPost(),
                1L,
                "신고 내용",
                ReportReason.OTHER
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
                "테스트 게시판 설명"
        );
    }
}