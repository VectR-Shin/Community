
package io.github.vectr.community.community.service.comment;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.community.dto.report.ReportRequestDTO;
import io.github.vectr.community.community.entity.board.Board;
import io.github.vectr.community.community.entity.comment.Comment;
import io.github.vectr.community.community.entity.comment.CommentReport;
import io.github.vectr.community.community.entity.post.Post;
import io.github.vectr.community.community.enums.ReportReason;
import io.github.vectr.community.community.exception.*;
import io.github.vectr.community.community.repository.board.BoardMemberSuspensionRepository;
import io.github.vectr.community.community.repository.board.BoardRepository;
import io.github.vectr.community.community.repository.comment.CommentReportRepository;
import io.github.vectr.community.community.repository.comment.CommentRepository;
import io.github.vectr.community.community.repository.post.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Test
    @DisplayName("정상적으로 댓글 신고를 생성한다.")
    void createCommentReport() {
        // given
        Long memberId = 1L;
        Long targetId = 10L;
        Long postId = 20L;
        Long boardId = 100L;

        ReportRequestDTO requestDTO = createRequestDTO(targetId);

        Comment comment = createComment(postId, 2L);
        Post post = createPost(boardId);

        when(commentRepository.findByIdAndDeletedAtIsNull(targetId))
                .thenReturn(Optional.of(comment));

        when(postRepository.findByIdAndDeletedAtIsNull(postId))
                .thenReturn(Optional.of(post));

        when(boardRepository.existsByIdAndDeletedAtIsNull(boardId))
                .thenReturn(true);

        when(boardMemberSuspensionRepository.existsCurrentBoardSuspension(
                eq(memberId), eq(boardId), any(Instant.class)
        )).thenReturn(false);

        // when
        commentReportCommandService.createCommentReport(memberId, requestDTO);

        // then
        verify(commentRepository).findByIdAndDeletedAtIsNull(targetId);

        verify(postRepository).findByIdAndDeletedAtIsNull(postId);

        verify(boardRepository).existsByIdAndDeletedAtIsNull(boardId);

        verify(boardMemberSuspensionRepository).existsCurrentBoardSuspension(
                eq(memberId), eq(boardId), any(Instant.class)
        );

        verify(commentReportRepository).save(any(CommentReport.class));
    }

    @Test
    @DisplayName("신고 대상 댓글이 존재하지 않거나 삭제된 경우 예외가 발생한다.")
    void createCommentReportWhenCommentNotFound() {
        // given
        Long memberId = 1L;
        Long targetId = 10L;

        ReportRequestDTO requestDTO = createRequestDTO(targetId);

        when(commentRepository.findByIdAndDeletedAtIsNull(targetId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                commentReportCommandService.createCommentReport(memberId, requestDTO)
        ).isInstanceOf(BusinessException.class);

        verifyNoInteractions(
                postRepository,
                boardRepository,
                boardMemberSuspensionRepository,
                commentReportRepository
        );
    }

    @Test
    @DisplayName("댓글이 속한 게시글이 존재하지 않거나 삭제된 경우 예외가 발생한다.")
    void createCommentReportWhenPostNotFound() {
        // given
        Long memberId = 1L;
        Long targetId = 10L;
        Long postId = 20L;

        ReportRequestDTO requestDTO = createRequestDTO(targetId);

        Comment comment = createComment(postId, 2L);

        when(commentRepository.findByIdAndDeletedAtIsNull(targetId))
                .thenReturn(Optional.of(comment));

        when(postRepository.findByIdAndDeletedAtIsNull(postId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                commentReportCommandService.createCommentReport(memberId, requestDTO)
        ).isInstanceOf(BusinessException.class);

        verifyNoInteractions(
                boardRepository,
                boardMemberSuspensionRepository,
                commentReportRepository
        );
    }

    @Test
    @DisplayName("댓글이 속한 게시판이 존재하지 않거나 삭제된 경우 예외가 발생한다.")
    void createCommentReportWhenBoardNotFound() {
        // given
        Long memberId = 1L;
        Long targetId = 10L;
        Long postId = 20L;
        Long boardId = 100L;

        ReportRequestDTO requestDTO = createRequestDTO(targetId);

        Comment comment = createComment(postId, 2L);
        Post post = createPost(boardId);

        when(commentRepository.findByIdAndDeletedAtIsNull(targetId))
                .thenReturn(Optional.of(comment));

        when(postRepository.findByIdAndDeletedAtIsNull(postId))
                .thenReturn(Optional.of(post));

        when(boardRepository.existsByIdAndDeletedAtIsNull(boardId))
                .thenReturn(false);

        // when & then
        assertThatThrownBy(() ->
                commentReportCommandService.createCommentReport(memberId, requestDTO)
        ).isInstanceOf(BusinessException.class);

        verify(boardMemberSuspensionRepository, never())
                .existsCurrentBoardSuspension(any(), any(), any());

        verify(commentReportRepository, never()).save(any(CommentReport.class));
    }

    @Test
    @DisplayName("해당 게시판에서 밴을 당한 사용자는 댓글을 신고할 수 없다.")
    void createCommentReportWhenBoardSuspended() {
        // given
        Long memberId = 1L;
        Long targetId = 10L;
        Long postId = 20L;
        Long boardId = 100L;

        ReportRequestDTO requestDTO = createRequestDTO(targetId);

        Comment comment = createComment(postId, 2L);
        Post post = createPost(boardId);

        when(commentRepository.findByIdAndDeletedAtIsNull(targetId))
                .thenReturn(Optional.of(comment));

        when(postRepository.findByIdAndDeletedAtIsNull(postId))
                .thenReturn(Optional.of(post));

        when(boardRepository.existsByIdAndDeletedAtIsNull(boardId))
                .thenReturn(true);

        when(boardMemberSuspensionRepository.existsCurrentBoardSuspension(
                eq(memberId), eq(boardId), any(Instant.class)
        )).thenReturn(true);

        // when & then
        assertThatThrownBy(() ->
                commentReportCommandService.createCommentReport(memberId, requestDTO)
        ).isInstanceOf(BusinessException.class);

        verify(commentReportRepository, never()).save(any(CommentReport.class));
    }

    @Test
    @DisplayName("자신이 작성한 댓글은 신고할 수 없다.")
    void createCommentReportWhenOwnComment() {
        // given
        Long memberId = 1L;
        Long targetId = 10L;
        Long postId = 20L;
        Long boardId = 100L;

        ReportRequestDTO requestDTO = createRequestDTO(targetId);

        Comment comment = createComment(postId, memberId);
        Post post = createPost(boardId);

        when(commentRepository.findByIdAndDeletedAtIsNull(targetId))
                .thenReturn(Optional.of(comment));

        when(postRepository.findByIdAndDeletedAtIsNull(postId))
                .thenReturn(Optional.of(post));

        when(boardRepository.existsByIdAndDeletedAtIsNull(boardId))
                .thenReturn(true);

        when(boardMemberSuspensionRepository.existsCurrentBoardSuspension(
                eq(memberId), eq(boardId), any(Instant.class)
        )).thenReturn(false);

        // when & then
        assertThatThrownBy(() ->
                commentReportCommandService.createCommentReport(memberId, requestDTO)
        ).isInstanceOf(BusinessException.class);

        verify(commentReportRepository, never()).save(any(CommentReport.class));
    }

    private ReportRequestDTO createRequestDTO(Long targetId) {
        ReportRequestDTO requestDTO = mock(ReportRequestDTO.class);

        when(requestDTO.targetId()).thenReturn(targetId);
        lenient().when(requestDTO.content()).thenReturn("신고 내용");
        lenient().when(requestDTO.reason()).thenReturn(ReportReason.OTHER);

        return requestDTO;
    }

    private Comment createComment(Long postId, Long memberId) {
        Post post = mock(Post.class);
        when(post.getId()).thenReturn(postId);

        Comment comment = mock(Comment.class);
        when(comment.getPost()).thenReturn(post);
        lenient().when(comment.getMemberId()).thenReturn(memberId);

        return comment;
    }

    private Post createPost(Long boardId) {
        Board board = mock(Board.class);
        when(board.getId()).thenReturn(boardId);

        Post post = mock(Post.class);
        when(post.getBoard()).thenReturn(board);

        return post;
    }
}
