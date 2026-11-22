
package io.github.vectr.community.community.service.post;

import io.github.vectr.community.common.exception.BusinessException;
import io.github.vectr.community.community.entity.board.Board;
import io.github.vectr.community.community.entity.post.Post;
import io.github.vectr.community.community.entity.post.PostReport;
import io.github.vectr.community.community.enums.PostType;
import io.github.vectr.community.community.dto.report.ReportRequestDTO;
import io.github.vectr.community.community.enums.ReportReason;
import io.github.vectr.community.community.enums.ReportTargetType;
import io.github.vectr.community.community.repository.board.BoardMemberSuspensionRepository;
import io.github.vectr.community.community.repository.board.BoardRepository;
import io.github.vectr.community.community.repository.post.PostReportRepository;
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

    private Long memberId;
    private Long postId;
    private Long boardId;

    private Post post;
    private Board board;

    private ReportRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        postId = 10L;
        boardId = 100L;

        post = mock(Post.class);
        board = mock(Board.class);

        requestDTO = new ReportRequestDTO(
                postId,
                ReportTargetType.values()[0],
                ReportReason.values()[0],
                "신고 내용"
        );
    }

    // 신고 대상 게시글이 존재하지 않거나 삭제된 경우 예외 발생
    @Test
    @DisplayName("신고 대상 게시글이 존재하지 않거나 삭제된 경우 예외가 발생한다.")
    void createPostReport_postNotFound_throwsException() {
        when(postRepository.findByIdAndPostTypeAndDeletedAtIsNull(
                postId, PostType.POST
        )).thenReturn(java.util.Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> postReportCommandService.createPostReport(memberId, requestDTO)
        );

        verify(boardRepository, never()).existsByIdAndDeletedAtIsNull(anyLong());
        verify(postReportRepository, never()).save(any(PostReport.class));
    }

    // 게시글이 속한 게시판이 존재하지 않거나 삭제된 경우 예외 발생
    @Test
    @DisplayName("게시글이 속한 게시판이 존재하지 않거나 삭제된 경우 예외가 발생한다.")
    void createPostReport_boardNotFound_throwsException() {
        givenValidPost();

        when(boardRepository.existsByIdAndDeletedAtIsNull(boardId))
                .thenReturn(false);

        assertThrows(
                BusinessException.class,
                () -> postReportCommandService.createPostReport(memberId, requestDTO)
        );

        verify(boardMemberSuspensionRepository, never())
                .existsCurrentBoardSuspension(anyLong(), anyLong(), any(Instant.class));

        verify(postReportRepository, never()).save(any(PostReport.class));
    }

    // 해당 게시판에서 정지된 사용자는 신고할 수 없음
    @Test
    @DisplayName("해당 게시판에서 정지된 사용자는 게시글을 신고할 수 없다.")
    void createPostReport_boardSuspended_throwsException() {
        givenValidPost();
        givenValidBoard();

        when(boardMemberSuspensionRepository.existsCurrentBoardSuspension(
                eq(memberId), eq(boardId), any(Instant.class)
        )).thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> postReportCommandService.createPostReport(memberId, requestDTO)
        );

        verify(postReportRepository, never())
                .existsByMemberIdAndPost_Id(anyLong(), anyLong());

        verify(postReportRepository, never()).save(any(PostReport.class));
    }

    // 동일 게시글에 이미 신고한 경우 예외 발생
    @Test
    @DisplayName("동일한 게시글에 이미 신고한 경우 예외가 발생한다.")
    void createPostReport_duplicateReport_throwsException() {
        givenValidPost();
        givenValidBoard();
        givenNotSuspended();

        when(post.getId()).thenReturn(postId);

        when(postReportRepository.existsByMemberIdAndPost_Id(memberId, postId))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> postReportCommandService.createPostReport(memberId, requestDTO)
        );

        verify(postReportRepository, never()).save(any(PostReport.class));
    }

    // 본인의 게시글은 신고할 수 없음
    @Test
    @DisplayName("본인이 작성한 게시글은 신고할 수 없다.")
    void createPostReport_ownPost_throwsException() {
        givenValidPost();
        givenValidBoard();
        givenNotSuspended();

        when(post.getId()).thenReturn(postId);
        when(postReportRepository.existsByMemberIdAndPost_Id(memberId, postId))
                .thenReturn(false);
        when(post.getMemberId()).thenReturn(memberId);

        assertThrows(
                BusinessException.class,
                () -> postReportCommandService.createPostReport(memberId, requestDTO)
        );

        verify(postReportRepository, never()).save(any(PostReport.class));
    }

    // 게시글 신고 생성 성공
    @Test
    @DisplayName("유효한 요청이면 게시글 신고가 생성된다.")
    void createPostReport_success() {
        givenValidPost();
        givenValidBoard();
        givenNotSuspended();

        when(post.getId()).thenReturn(postId);
        when(postReportRepository.existsByMemberIdAndPost_Id(memberId, postId))
                .thenReturn(false);
        when(post.getMemberId()).thenReturn(2L);

        postReportCommandService.createPostReport(memberId, requestDTO);

        ArgumentCaptor<PostReport> captor =
                ArgumentCaptor.forClass(PostReport.class);

        verify(postReportRepository).save(captor.capture());

        PostReport savedReport = captor.getValue();

        assertNotNull(savedReport);
    }

    // 신고 대상 조회 실패 시 게시판 검사는 수행하지 않음
    @Test
    @DisplayName("신고 대상 게시글 조회에 실패하면 게시판 검사는 수행하지 않는다.")
    void createPostReport_postNotFound_boardValidationNotExecuted() {
        when(postRepository.findByIdAndPostTypeAndDeletedAtIsNull(
                postId, PostType.POST
        )).thenReturn(java.util.Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> postReportCommandService.createPostReport(memberId, requestDTO)
        );

        verify(boardRepository, never()).existsByIdAndDeletedAtIsNull(anyLong());
    }

    // 중복 신고 시 본인 게시글 검사 및 저장은 수행하지 않음
    @Test
    @DisplayName("중복 신고인 경우 본인 게시글 여부를 검사하지 않고 신고를 저장하지 않는다.")
    void createPostReport_duplicateReport_selfReportValidationNotExecuted() {
        givenValidPost();
        givenValidBoard();
        givenNotSuspended();

        when(post.getId()).thenReturn(postId);
        when(postReportRepository.existsByMemberIdAndPost_Id(memberId, postId))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> postReportCommandService.createPostReport(memberId, requestDTO)
        );

        verify(post, never()).getMemberId();
        verify(postReportRepository, never()).save(any(PostReport.class));
    }

    private void givenValidPost() {
        when(postRepository.findByIdAndPostTypeAndDeletedAtIsNull(
                postId, PostType.POST
        )).thenReturn(java.util.Optional.of(post));

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
