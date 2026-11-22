
package io.github.vectr.community.community.service.comment;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.community.entity.board.Board;
import io.github.vectr.community.community.entity.comment.Comment;
import io.github.vectr.community.community.entity.comment.CommentReport;
import io.github.vectr.community.community.entity.post.Post;
import io.github.vectr.community.community.dto.report.ReportRequestDTO;
import io.github.vectr.community.community.enums.ReportReason;
import io.github.vectr.community.community.enums.ReportTargetType;
import io.github.vectr.community.community.repository.board.BoardMemberSuspensionRepository;
import io.github.vectr.community.community.repository.board.BoardRepository;
import io.github.vectr.community.community.repository.comment.CommentReportRepository;
import io.github.vectr.community.community.repository.comment.CommentRepository;
import io.github.vectr.community.community.repository.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentReportCommandServiceImplTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardMemberSuspensionRepository boardMemberSuspensionRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentReportRepository commentReportRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentReportCommandServiceImpl commentReportCommandService;

    private Long memberId;
    private Long commentId;
    private Long postId;
    private Long boardId;

    private Comment comment;
    private Post post;
    private Board board;

    private ReportRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        commentId = 10L;
        postId = 100L;
        boardId = 1000L;

        comment = mock(Comment.class);
        post = mock(Post.class);
        board = mock(Board.class);

        requestDTO = new ReportRequestDTO(
                commentId,
                ReportTargetType.values()[0],
                ReportReason.values()[0],
                "신고 내용"
        );
    }

    // 신고 대상 댓글이 존재하지 않거나 삭제된 경우 예외 발생
    @Test
    @DisplayName("신고 대상 댓글이 존재하지 않거나 삭제된 경우 예외가 발생한다.")
    void createCommentReport_commentNotFound_throwsException() {
        when(commentRepository.findByIdAndDeletedAtIsNull(commentId))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> commentReportCommandService.createCommentReport(memberId, requestDTO)
        );

        verify(postRepository, never()).findByIdAndDeletedAtIsNull(anyLong());
        verify(commentReportRepository, never()).save(any(CommentReport.class));
    }

    // 댓글이 속한 게시글이 존재하지 않거나 삭제된 경우 예외 발생
    @Test
    @DisplayName("댓글이 속한 게시글이 존재하지 않거나 삭제된 경우 예외가 발생한다.")
    void createCommentReport_postNotFound_throwsException() {
        givenValidComment();

        when(postRepository.findByIdAndDeletedAtIsNull(postId))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> commentReportCommandService.createCommentReport(memberId, requestDTO)
        );

        verify(boardRepository, never()).existsByIdAndDeletedAtIsNull(anyLong());
        verify(commentReportRepository, never()).save(any(CommentReport.class));
    }

    // 댓글이 속한 게시판이 존재하지 않거나 삭제된 경우 예외 발생
    @Test
    @DisplayName("댓글이 속한 게시판이 존재하지 않거나 삭제된 경우 예외가 발생한다.")
    void createCommentReport_boardNotFound_throwsException() {
        givenValidComment();
        givenValidPost();
        givenPostBoard();

        when(boardRepository.existsByIdAndDeletedAtIsNull(boardId))
                .thenReturn(false);

        assertThrows(
                BusinessException.class,
                () -> commentReportCommandService.createCommentReport(memberId, requestDTO)
        );

        verify(boardMemberSuspensionRepository, never())
                .existsCurrentBoardSuspension(anyLong(), anyLong(), any(Instant.class));

        verify(commentReportRepository, never()).save(any(CommentReport.class));
    }

    // 해당 게시판에서 정지된 사용자는 신고할 수 없음
    @Test
    @DisplayName("해당 게시판에서 정지된 사용자는 댓글을 신고할 수 없다.")
    void createCommentReport_boardSuspended_throwsException() {
        givenValidComment();
        givenValidPost();
        givenPostBoard();
        givenValidBoard();

        when(boardMemberSuspensionRepository.existsCurrentBoardSuspension(
                eq(memberId), eq(boardId), any(Instant.class)
        )).thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> commentReportCommandService.createCommentReport(memberId, requestDTO)
        );

        verify(commentReportRepository, never())
                .existsByMemberIdAndComment_Id(anyLong(), anyLong());

        verify(commentReportRepository, never()).save(any(CommentReport.class));
    }

    // 동일 댓글에 이미 신고한 경우 예외 발생
    @Test
    @DisplayName("동일한 댓글에 이미 신고한 경우 예외가 발생한다.")
    void createCommentReport_duplicateReport_throwsException() {
        givenValidComment();
        givenValidPost();
        givenPostBoard();
        givenValidBoard();
        givenNotSuspended();

        when(comment.getId()).thenReturn(commentId);

        when(commentReportRepository.existsByMemberIdAndComment_Id(memberId, commentId))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> commentReportCommandService.createCommentReport(memberId, requestDTO)
        );

        verify(commentReportRepository, never()).save(any(CommentReport.class));
    }

    // 본인의 댓글은 신고할 수 없음
    @Test
    @DisplayName("본인이 작성한 댓글은 신고할 수 없다.")
    void createCommentReport_ownComment_throwsException() {
        givenValidComment();
        givenValidPost();
        givenPostBoard();
        givenValidBoard();
        givenNotSuspended();

        when(comment.getId()).thenReturn(commentId);

        when(commentReportRepository.existsByMemberIdAndComment_Id(memberId, commentId))
                .thenReturn(false);

        when(comment.getMemberId()).thenReturn(memberId);

        assertThrows(
                BusinessException.class,
                () -> commentReportCommandService.createCommentReport(memberId, requestDTO)
        );

        verify(commentReportRepository, never()).save(any(CommentReport.class));
    }

    // 댓글 신고 생성 성공
    @Test
    @DisplayName("유효한 요청이면 댓글 신고가 생성된다.")
    void createCommentReport_success() {
        givenValidComment();
        givenValidPost();
        givenPostBoard();
        givenValidBoard();
        givenNotSuspended();

        when(comment.getId()).thenReturn(commentId);

        when(commentReportRepository.existsByMemberIdAndComment_Id(memberId, commentId))
                .thenReturn(false);

        when(comment.getMemberId()).thenReturn(2L);

        commentReportCommandService.createCommentReport(memberId, requestDTO);

        ArgumentCaptor<CommentReport> captor =
                ArgumentCaptor.forClass(CommentReport.class);

        verify(commentReportRepository).save(captor.capture());

        CommentReport savedReport = captor.getValue();

        assertNotNull(savedReport);
    }

    // 중복 신고 시 본인 댓글 검사 및 저장은 수행하지 않음
    @Test
    @DisplayName("중복 신고인 경우 본인 댓글 여부를 검사하지 않고 신고를 저장하지 않는다.")
    void createCommentReport_duplicateReport_selfReportValidationNotExecuted() {
        givenValidComment();
        givenValidPost();
        givenPostBoard();
        givenValidBoard();
        givenNotSuspended();

        when(comment.getId()).thenReturn(commentId);

        when(commentReportRepository.existsByMemberIdAndComment_Id(memberId, commentId))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> commentReportCommandService.createCommentReport(memberId, requestDTO)
        );

        verify(comment, never()).getMemberId();
        verify(commentReportRepository, never()).save(any(CommentReport.class));
    }

    private void givenValidComment() {
        when(commentRepository.findByIdAndDeletedAtIsNull(commentId))
                .thenReturn(java.util.Optional.of(comment));

        when(comment.getPost()).thenReturn(post);
        when(post.getId()).thenReturn(postId);
    }

    private void givenValidPost() {
        when(postRepository.findByIdAndDeletedAtIsNull(postId))
                .thenReturn(java.util.Optional.of(post));
    }

    private void givenPostBoard() {
        when(post.getBoard()).thenReturn(board);
        when(board.getId()).thenReturn(boardId);
    }

    private void givenValidBoard() {
        when(boardRepository.existsByIdAndDeletedAtIsNull(boardId))
                .thenReturn(true);
    }

    private void givenNotSuspended() {
        when(boardMemberSuspensionRepository.existsCurrentBoardSuspension(
                eq(memberId), eq(boardId), any(Instant.class)
        )).thenReturn(false);
    }
}
