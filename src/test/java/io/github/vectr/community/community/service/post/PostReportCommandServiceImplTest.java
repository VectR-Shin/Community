
package io.github.vectr.community.community.service.post;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.community.dto.report.ReportRequestDTO;
import io.github.vectr.community.community.entity.board.Board;
import io.github.vectr.community.community.entity.post.Post;
import io.github.vectr.community.community.entity.post.PostReport;
import io.github.vectr.community.community.enums.PostType;
import io.github.vectr.community.community.enums.ReportReason;
import io.github.vectr.community.community.repository.board.BoardMemberSuspensionRepository;
import io.github.vectr.community.community.repository.board.BoardRepository;
import io.github.vectr.community.community.repository.post.PostReportRepository;
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
class PostReportCommandServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private BoardMemberSuspensionRepository boardMemberSuspensionRepository;

    @Mock
    private PostReportRepository postReportRepository;

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private PostReportCommandServiceImpl postReportCommandService;

    @Test
    @DisplayName("정상적으로 게시글 신고를 생성한다.")
    void createPostReport() {
        // given
        Long memberId = 1L;
        Long targetId = 10L;
        Long boardId = 100L;

        ReportRequestDTO requestDTO = createRequestDTO(targetId);

        Post post = createPost(boardId, 2L);

        when(postRepository.findByIdAndPostTypeAndDeletedAtIsNull(
                targetId, PostType.POST
        )).thenReturn(Optional.of(post));

        when(boardRepository.existsByIdAndDeletedAtIsNull(boardId))
                .thenReturn(true);

        when(boardMemberSuspensionRepository.existsCurrentBoardSuspension(
                eq(memberId), eq(boardId), any(Instant.class)
        )).thenReturn(false);

        // when
        postReportCommandService.createPostReport(memberId, requestDTO);

        // then
        verify(postRepository).findByIdAndPostTypeAndDeletedAtIsNull(
                targetId, PostType.POST
        );

        verify(boardRepository).existsByIdAndDeletedAtIsNull(boardId);

        verify(boardMemberSuspensionRepository).existsCurrentBoardSuspension(
                eq(memberId), eq(boardId), any(Instant.class)
        );

        verify(postReportRepository).save(any(PostReport.class));
    }

    @Test
    @DisplayName("신고 대상 게시글이 존재하지 않거나 삭제된 경우 예외가 발생한다.")
    void createPostReportWhenPostNotFound() {
        // given
        Long memberId = 1L;
        Long targetId = 10L;

        ReportRequestDTO requestDTO = createRequestDTO(targetId);

        when(postRepository.findByIdAndPostTypeAndDeletedAtIsNull(
                targetId, PostType.POST
        )).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                postReportCommandService.createPostReport(memberId, requestDTO)
        ).isInstanceOf(BusinessException.class);

        verify(postReportRepository, never()).save(any(PostReport.class));
        verifyNoInteractions(boardRepository, boardMemberSuspensionRepository);
    }

    @Test
    @DisplayName("게시글이 속한 게시판이 존재하지 않거나 삭제된 경우 예외가 발생한다.")
    void createPostReportWhenBoardNotFound() {
        // given
        Long memberId = 1L;
        Long targetId = 10L;
        Long boardId = 100L;

        ReportRequestDTO requestDTO = createRequestDTO(targetId);

        Post post = createPost(boardId, 2L);

        when(postRepository.findByIdAndPostTypeAndDeletedAtIsNull(
                targetId, PostType.POST
        )).thenReturn(Optional.of(post));

        when(boardRepository.existsByIdAndDeletedAtIsNull(boardId))
                .thenReturn(false);

        // when & then
        assertThatThrownBy(() ->
                postReportCommandService.createPostReport(memberId, requestDTO)
        ).isInstanceOf(BusinessException.class);

        verify(boardMemberSuspensionRepository, never())
                .existsCurrentBoardSuspension(any(), any(), any());

        verify(postReportRepository, never()).save(any(PostReport.class));
    }

    @Test
    @DisplayName("해당 게시판에서 밴을 당한 사용자는 게시글을 신고할 수 없다.")
    void createPostReportWhenBoardSuspended() {
        // given
        Long memberId = 1L;
        Long targetId = 10L;
        Long boardId = 100L;

        ReportRequestDTO requestDTO = createRequestDTO(targetId);

        Post post = createPost(boardId, 2L);

        when(postRepository.findByIdAndPostTypeAndDeletedAtIsNull(
                targetId, PostType.POST
        )).thenReturn(Optional.of(post));

        when(boardRepository.existsByIdAndDeletedAtIsNull(boardId))
                .thenReturn(true);

        when(boardMemberSuspensionRepository.existsCurrentBoardSuspension(
                eq(memberId), eq(boardId), any(Instant.class)
        )).thenReturn(true);

        // when & then
        assertThatThrownBy(() ->
                postReportCommandService.createPostReport(memberId, requestDTO)
        ).isInstanceOf(BusinessException.class);

        verify(postReportRepository, never()).save(any(PostReport.class));
    }

    @Test
    @DisplayName("자신이 작성한 게시글은 신고할 수 없다.")
    void createPostReportWhenOwnPost() {
        // given
        Long memberId = 1L;
        Long targetId = 10L;
        Long boardId = 100L;

        ReportRequestDTO requestDTO = createRequestDTO(targetId);

        Post post = createPost(boardId, memberId);

        when(postRepository.findByIdAndPostTypeAndDeletedAtIsNull(
                targetId, PostType.POST
        )).thenReturn(Optional.of(post));

        when(boardRepository.existsByIdAndDeletedAtIsNull(boardId))
                .thenReturn(true);

        when(boardMemberSuspensionRepository.existsCurrentBoardSuspension(
                eq(memberId), eq(boardId), any(Instant.class)
        )).thenReturn(false);

        // when & then
        assertThatThrownBy(() ->
                postReportCommandService.createPostReport(memberId, requestDTO)
        ).isInstanceOf(BusinessException.class);

        verify(postReportRepository, never()).save(any(PostReport.class));
    }

    private ReportRequestDTO createRequestDTO(Long targetId) {
        ReportRequestDTO requestDTO = mock(ReportRequestDTO.class);

        when(requestDTO.targetId()).thenReturn(targetId);
        lenient().when(requestDTO.content()).thenReturn("신고 내용");
        lenient().when(requestDTO.reason()).thenReturn(ReportReason.OTHER);

        return requestDTO;
    }

    private Post createPost(Long boardId, Long memberId) {
        Board board = mock(Board.class);
        when(board.getId()).thenReturn(boardId);

        Post post = mock(Post.class);
        when(post.getBoard()).thenReturn(board);

        lenient().when(post.getMemberId()).thenReturn(memberId);

        return post;
    }
}
